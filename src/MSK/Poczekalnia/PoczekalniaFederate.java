package MSK.Poczekalnia;

import MSK.Rejestracja.RejestracjaAmbassador;
import MSK.Rejestracja.RejestracjaFederate;
import hla.rti.*;
import hla.rti.jlc.EncodingHelpers;
import hla.rti.jlc.RtiFactoryFactory;
import org.portico.impl.hla13.types.DoubleTime;
import org.portico.impl.hla13.types.DoubleTimeInterval;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.Random;

public class PoczekalniaFederate {
    public static final String READY_TO_RUN = "ReadyToRun";
    protected int rejestracjaHlaHandle;

    private RTIambassador rtiamb;
    private PoczekalniaAmbassador fedamb;
    private final double timeStep = 10.0;


    public void runFederate() throws RTIexception {
        rtiamb = RtiFactoryFactory.getRtiFactory().createRtiAmbassador();

        try
        {
            File fom = new File( "msk.fed" );
            rtiamb.createFederationExecution( "ExampleFederation",
                    fom.toURI().toURL() );
            log( "Created Federation" );
        }
        catch( FederationExecutionAlreadyExists exists )
        {
            log( "Didn't create federation, it already existed" );
        }
        catch( MalformedURLException urle )
        {
            log( "Exception processing fom: " + urle.getMessage() );
            urle.printStackTrace();
            return;
        }

        fedamb = new PoczekalniaAmbassador();
        rtiamb.joinFederationExecution( "PoczekalniaFederate", "ExampleFederation", fedamb );
        log( "Joined Federation as PoczekalniaFederate");

        rtiamb.registerFederationSynchronizationPoint( READY_TO_RUN, null );

        while( fedamb.isAnnounced == false )
        {
            rtiamb.tick();
        }

        waitForUser();

        rtiamb.synchronizationPointAchieved( READY_TO_RUN );
        log( "Achieved sync point: " +READY_TO_RUN+ ", waiting for federation..." );
        while( fedamb.isReadyToRun == false )
        {
            rtiamb.tick();
        }

        enableTimePolicy();

        publishAndSubscribe();

        registerPoczekalniaObject();

        while (fedamb.running) {
            advanceTime(randomTime());

            //updateHLAObject(fedamb.federateTime + fedamb.federateLookahead);
            //sendInteraction(fedamb.federateTime + fedamb.federateLookahead);
            rtiamb.tick();
        }

    }

    private void registerPoczekalniaObject() throws RTIexception {
        int classHandleCreate = rtiamb.getObjectClassHandle("ObjectRoot.Poczekalnia");
        this.rejestracjaHlaHandle = rtiamb.registerObjectInstance(classHandleCreate);

    }

    public void updateHLAObject(int Handle) throws RTIexception{
        /*SuppliedAttributes attributes =
                RtiFactoryFactory.getRtiFactory().createSuppliedAttributes();
        for (int i: this.pacjentHlaHandle) {
            int classHandle = rtiamb.getObjectClass(pacjentHlaHandle[i]);
            int miejsceHandle = rtiamb.getAttributeHandle( "miejsce", classHandle );
            byte[] miejscekValue = ByteBuffer.allocate(4).putInt(i).array();
            //zmiana miejsca pobytu pacjenta

            attributes.add(miejsceHandle, miejscekValue);
            LogicalTime logicalTime = convertTime( time );
            rtiamb.updateAttributeValues( pacjentHlaHandle[i], attributes, "actualize".getBytes(), logicalTime );
        }*/
    }

    private void waitForUser()
    {
        log( " >>>>>>>>>> Press Enter to Continue <<<<<<<<<<" );
        BufferedReader reader = new BufferedReader( new InputStreamReader(System.in) );
        try
        {
            reader.readLine();
        }
        catch( Exception e )
        {
            log( "Error while waiting for user input: " + e.getMessage() );
            e.printStackTrace();
        }
    }

    private void enableTimePolicy() throws RTIexception
    {
        LogicalTime currentTime = convertTime( fedamb.federateTime );
        LogicalTimeInterval lookahead = convertInterval( fedamb.federateLookahead );

        this.rtiamb.enableTimeRegulation( currentTime, lookahead );

        while( fedamb.isRegulating == false )
        {
            rtiamb.tick();
        }

        this.rtiamb.enableTimeConstrained();

        while( fedamb.isConstrained == false )
        {
            rtiamb.tick();
        }
    }

    private void sendInteraction(double timeStep) throws RTIexception {
    }

    private void publishAndSubscribe() throws RTIexception {
        int classHandle = rtiamb.getObjectClassHandle("ObjectRoot.Poczekalnia");
        int listaHandle    = rtiamb.getAttributeHandle( "lista", classHandle );
        int iloscMiejscHandle    = rtiamb.getAttributeHandle( "ilosc_miejsc", classHandle );
        int listaDostepnychLekarzyHandle    = rtiamb.getAttributeHandle( "lista_dostepnych_lekarzy", classHandle );

        AttributeHandleSet attributes = RtiFactoryFactory.getRtiFactory().createAttributeHandleSet();
        attributes.add( listaHandle );
        attributes.add( iloscMiejscHandle );
        attributes.add( listaDostepnychLekarzyHandle );

        rtiamb.publishObjectClass(classHandle, attributes);

        int przeniesienieHandle = rtiamb.getInteractionClassHandle( "InteractionRoot.Przeniesienie_pacjenta" );
        fedamb.przeniesienieHlaHandle = przeniesienieHandle;
        rtiamb.publishInteractionClass(przeniesienieHandle);
        rtiamb.subscribeInteractionClass(przeniesienieHandle);

    }

    private void advanceTime( double timestep ) throws RTIexception
    {
        //log("requesting time advance for: " + timestep);

        // request the advance
        fedamb.isAdvancing = true;
        LogicalTime newTime = convertTime( fedamb.federateTime + timestep );
        rtiamb.timeAdvanceRequest( newTime );
        while( fedamb.isAdvancing )
        {
            rtiamb.tick();
        }
        fedamb.federateTime +=timestep;
    }

    private double randomTime() {
        Random r = new Random();
        return 1 +(4 * r.nextDouble());
    }

    private LogicalTime convertTime( double time )
    {
        // PORTICO SPECIFIC!!
        return new DoubleTime( time );
    }

    /**
     * Same as for {@link #convertTime(double)}
     */
    private LogicalTimeInterval convertInterval( double time )
    {
        // PORTICO SPECIFIC!!
        return new DoubleTimeInterval( time );
    }

    private void log( String message )
    {
        System.out.println( "PoczekalniaFederate   : " + message );
    }

    public static void main(String[] args) {
        try {
            new PoczekalniaFederate().runFederate();
        } catch (RTIexception rtIexception) {
            rtIexception.printStackTrace();
        }
    }
}

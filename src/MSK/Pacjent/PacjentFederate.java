package MSK.Pacjent;

import hla.rti.*;
import hla.rti.jlc.EncodingHelpers;
import hla.rti.jlc.RtiFactoryFactory;
import org.portico.impl.hla13.types.DoubleTime;
import org.portico.impl.hla13.types.DoubleTimeInterval;

import java.io.File;
import java.net.MalformedURLException;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;

public class PacjentFederate {
    public static final String READY_TO_RUN = "ReadyToRun";
    public static int idstatic=0;
    public static int[] pacjentHlaHandle = new int[500]; // maksymalna ilość przyjętych pacjentów
    /***
     * @value
     * 0 = rejetracja
     * 1 = poczekalnia
     * 2 = lekarz
     * 3 = gabinet
     */
    private int miejsce;


    private RTIambassador rtiamb;
    private PacjentAmbassador fedamb;
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

        fedamb = new PacjentAmbassador();
        rtiamb.joinFederationExecution( "PacjentFederate", "ExampleFederation", fedamb );
        log( "Joined Federation as PacjentFederate");

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

        while (fedamb.running) {
            advanceTime(randomTime());
            registerPacjentObject(fedamb.federateTime + fedamb.federateLookahead);
            //updateHLAObject(fedamb.federateTime + fedamb.federateLookahead);
            sendInteraction(fedamb.federateTime + fedamb.federateLookahead);
            rtiamb.tick();
        }

    }

    private void registerPacjentObject(double time) throws RTIexception {
        int classHandleCreate = rtiamb.getObjectClassHandle("ObjectRoot.Pacjent");
        pacjentHlaHandle[idstatic] = rtiamb.registerObjectInstance(classHandleCreate);

        SuppliedAttributes attributes = RtiFactoryFactory.getRtiFactory().createSuppliedAttributes();
        int classHandle = rtiamb.getObjectClass(pacjentHlaHandle[idstatic]);
        int idHandle = rtiamb.getAttributeHandle( "id_pacjenta", classHandle );
        byte[] idValue = EncodingHelpers.encodeInt(idstatic);
        int miejsceHandle = rtiamb.getAttributeHandle( "miejsce", classHandle );
        byte[] miejsceValue = EncodingHelpers.encodeInt(0);

        attributes.add(idHandle, idValue);
        attributes.add(miejsceHandle, miejsceValue);
        LogicalTime logicalTime = convertTime( time );
        rtiamb.updateAttributeValues( pacjentHlaHandle[idstatic], attributes, "actualize pacjent".getBytes(), logicalTime );
        log("Przybył pacjent nr " + ByteBuffer.wrap(idValue).getInt() + " jego handle wynosi " + pacjentHlaHandle[idstatic]);
    }


    private void updateHLAObject(double time) throws RTIexception{
        SuppliedAttributes attributes =
                RtiFactoryFactory.getRtiFactory().createSuppliedAttributes();
        for (int i: this.pacjentHlaHandle) {
            int classHandle = rtiamb.getObjectClass(pacjentHlaHandle[i]);
            int miejsceHandle = rtiamb.getAttributeHandle( "miejsce", classHandle );
            byte[] miejscekValue = ByteBuffer.allocate(4).putInt(i).array();
            //zmiana miejsca pobytu pacjenta

            attributes.add(miejsceHandle, miejscekValue);
            LogicalTime logicalTime = convertTime( time );
            rtiamb.updateAttributeValues( pacjentHlaHandle[i], attributes, "actualize".getBytes(), logicalTime );
        }
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
        SuppliedParameters parameters = RtiFactoryFactory.getRtiFactory().createSuppliedParameters();
        byte[] id_pacjenta = EncodingHelpers.encodeInt(idstatic);
        byte[] godzina_wejscia = EncodingHelpers.encodeDouble(timeStep);

        int interactionHandle = rtiamb.getInteractionClassHandle("InteractionRoot.Wejscie_do_przychodni");
        int idpacjentaHandle = rtiamb.getParameterHandle( "id_pacjenta", interactionHandle );
        int godzinawejsciaHandle = rtiamb.getParameterHandle( "godzina_wejscia", interactionHandle );

        parameters.add(idpacjentaHandle,id_pacjenta );
        parameters.add(godzinawejsciaHandle,godzina_wejscia );
        LogicalTime time = convertTime( timeStep );
        rtiamb.sendInteraction( interactionHandle, parameters, "tag".getBytes(), time );
        log("Send interaction ");
        idstatic++;
    }

    private void publishAndSubscribe() throws RTIexception {
        int classHandle = rtiamb.getObjectClassHandle("ObjectRoot.Pacjent");
        int idHandle    = rtiamb.getAttributeHandle( "id_pacjenta", classHandle );
        int miejsceHandle    = rtiamb.getAttributeHandle( "miejsce", classHandle );

        AttributeHandleSet attributes = RtiFactoryFactory.getRtiFactory().createAttributeHandleSet();
        attributes.add( idHandle );
        attributes.add( miejsceHandle );

        rtiamb.publishObjectClass(classHandle, attributes);
       // rtiamb.subscribeObjectClassAttributes(classHandle, attributes);

        int wejscieHandle = rtiamb.getInteractionClassHandle( "InteractionRoot.Wejscie_do_przychodni" );
        fedamb.wejscieHandle = wejscieHandle;
        rtiamb.publishInteractionClass(wejscieHandle);
    }

    private void advanceTime( double timestep ) throws RTIexception
    {
        log("requesting time advance for: " + timestep);

        // request the advance
        fedamb.isAdvancing = true;
        LogicalTime newTime = convertTime( fedamb.federateTime + timestep );
        rtiamb.timeAdvanceRequest( newTime );
        while( fedamb.isAdvancing )
        {
            rtiamb.tick();
        }
        fedamb.federateTime +=timestep;
        log("Pacjent time: " + fedamb.federateTime);
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
        System.out.println( "PacjentFederate   : " + message );
    }

    public static void main(String[] args) {
        try {
            new PacjentFederate().runFederate();
        } catch (RTIexception rtIexception) {
            rtIexception.printStackTrace();
        }
    }
}

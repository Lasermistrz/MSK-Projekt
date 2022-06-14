package MSK.Gabinet;

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

public class GabinetFederate {
    public static final String READY_TO_RUN = "ReadyToRun";
    private RTIambassador rtiamb;
    private GabinetAmbassador fedamb;
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

        fedamb = new GabinetAmbassador();
        rtiamb.joinFederationExecution( "GabinetFederate", "ExampleFederation", fedamb );
        log( "Joined Federation as GabinetFederate");

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
            advanceTime(3*randomTime());
                if(GabinetAmbassador.lista.size()>0){
                    sendInteraction(fedamb.federateTime+ 2*randomTime(),GabinetAmbassador.lista.get(0));
                    GabinetAmbassador.lista.remove(0);
                }
            rtiamb.tick();
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

    private void sendInteraction(double timeStep,int id_pacjenta) throws RTIexception {
        LogicalTime time = convertTime(timeStep);

        SuppliedParameters parameters = RtiFactoryFactory.getRtiFactory().createSuppliedParameters();
        int przeniesienieHandle = rtiamb.getInteractionClassHandle( "InteractionRoot.Przeniesienie_pacjenta" );
        int idPacjentaHandlePar = rtiamb.getParameterHandle("id_pacjenta",przeniesienieHandle);
        int miejsceHandlePar = rtiamb.getParameterHandle("miejsce_koncowe",przeniesienieHandle);
        byte[] idPacjenta = EncodingHelpers.encodeInt(id_pacjenta);
        byte[] miejsce_koncowe = EncodingHelpers.encodeInt(5); // ukończona obsługa
        parameters.add(idPacjentaHandlePar,idPacjenta);
        parameters.add(miejsceHandlePar,miejsce_koncowe);
        rtiamb.sendInteraction(przeniesienieHandle,parameters,"tag".getBytes(),time);
        log("Obsluzono pacjenta nr " + id_pacjenta );
    }

    private void publishAndSubscribe() throws RTIexception {

        int przeniesienieHandle = rtiamb.getInteractionClassHandle( "InteractionRoot.Przeniesienie_pacjenta" );
        fedamb.przeniesienieHlaHandle = przeniesienieHandle;
        rtiamb.subscribeInteractionClass(przeniesienieHandle);
        rtiamb.publishInteractionClass(przeniesienieHandle);
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
        System.out.println( "GabinetFederate   : " + message );
    }

    public static void main(String[] args) {
        try {
            new GabinetFederate().runFederate();
        } catch (RTIexception rtIexception) {
            rtIexception.printStackTrace();
        }
    }
}

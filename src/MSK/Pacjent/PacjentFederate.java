package MSK.Pacjent;

import MSK.Parameters;
import hla.rti.RTIinternalError;
import hla.rti1516e.*;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAfloat64BE;
import hla.rti1516e.encoding.HLAinteger32BE;
import hla.rti1516e.exceptions.FederationExecutionAlreadyExists;
import hla.rti1516e.exceptions.RTIexception;
import hla.rti1516e.time.HLAfloat64Interval;
import hla.rti1516e.time.HLAfloat64Time;
import hla.rti1516e.time.HLAfloat64TimeFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Random;

public class PacjentFederate {
    public static final String READY_TO_RUN = "ReadyToRun";
    public static int idstatic=0;

    private RTIambassador rtiamb;
    private PacjentAmbassador fedamb;
    private final double timeStep = 1.0;
    private HLAfloat64TimeFactory timeFactory; // set when we join
    protected EncoderFactory encoderFactory;     // set when we join
    protected static ObjectClassHandle pacjentHandle;
    protected static AttributeHandle idPacjentaHandle;
    protected static InteractionClassHandle wejscieHandle;
    protected static ParameterHandle godzinaWejsciaHandle;
    protected static ParameterHandle idPacjentaWejscieHandle;

    public void runFederate(String federateName) throws RTIexception, MalformedURLException, RTIinternalError {
        rtiamb = RtiFactoryFactory.getRtiFactory().getRtiAmbassador();
        encoderFactory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();

        log( "Connecting..." );
        fedamb = new PacjentAmbassador( this );
        rtiamb.connect( fedamb, CallbackModel.HLA_EVOKED );

        log( "Creating Federation..." );

        try
        {
            URL[] modules = new URL[]{
                    (new File("foms/msk.xml")).toURI().toURL()
            };

            rtiamb.createFederationExecution( "ExampleFederation", modules );
            log( "Created Federation" );
        }
        catch( FederationExecutionAlreadyExists exists )
        {
            log( "Didn't create federation, it already existed" );
        }
        catch( MalformedURLException urle )
        {
            log( "Exception loading one of the FOM modules from disk: " + urle.getMessage() );
            urle.printStackTrace();
            return;
        }

        URL[] joinModules = new URL[]{
                (new File("foms/msk.xml")).toURI().toURL()
        };

        rtiamb.joinFederationExecution( federateName,            // name for the federate
                "ExampleFederateType",   // federate type
                "ExampleFederation",     // name of federation
                joinModules );           // modules we want to add

        log( "Joined Federation as " + federateName );

        this.timeFactory = (HLAfloat64TimeFactory)rtiamb.getTimeFactory();

        rtiamb.registerFederationSynchronizationPoint( READY_TO_RUN, null );

        while( fedamb.isAnnounced == false )
        {
            rtiamb.evokeMultipleCallbacks( 0.1, 0.2 );
        }

        waitForUser();

        rtiamb.synchronizationPointAchieved( READY_TO_RUN );
        log( "Achieved sync point: " +READY_TO_RUN+ ", waiting for federation..." );
        while( fedamb.isReadyToRun == false )
        {
            rtiamb.evokeMultipleCallbacks( 0.1, 0.2 );
        }

        enableTimePolicy();

        publishAndSubscribe();

        while (fedamb.running) {
            if (fedamb.federateTime < 480-Parameters.przyplywPacjentow*5){
            advanceTime(Parameters.przyplywPacjentow *randomTime());
            sendInteraction(fedamb.federateTime + fedamb.federateLookahead);
            rtiamb.evokeMultipleCallbacks( 0.1, 0.2 );
           }else {
                advanceTime(randomTime());
                rtiamb.evokeMultipleCallbacks( 0.1, 0.2 );
            }
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
        HLAfloat64Interval lookahead = timeFactory.makeInterval( fedamb.federateLookahead );

        this.rtiamb.enableTimeRegulation( lookahead );

        while( fedamb.isRegulating == false )
        {
            rtiamb.evokeMultipleCallbacks( 0.1, 0.2 );
        }
        this.rtiamb.enableTimeConstrained();

        while( fedamb.isConstrained == false )
        {
            rtiamb.evokeMultipleCallbacks( 0.1, 0.2 );
        }
    }

    private void sendInteraction(double timeStep) throws RTIexception {
        HLAfloat64Time time = timeFactory.makeTime(fedamb.federateTime + fedamb.federateLookahead);
        ParameterHandleValueMap parameters = rtiamb.getParameterHandleValueMapFactory().create(0);
        HLAinteger32BE idPacjenta = encoderFactory.createHLAinteger32BE(idstatic);
        HLAfloat64BE godzina_wejscia = encoderFactory.createHLAfloat64BE(timeStep);

        parameters.put(idPacjentaWejscieHandle,idPacjenta.toByteArray());
        parameters.put(godzinaWejsciaHandle,godzina_wejscia.toByteArray());

        rtiamb.sendInteraction( wejscieHandle, parameters, "tag".getBytes(), time );
        log("Przybyl pacjent nr " + idstatic);
        //log("Send interaction ");
        idstatic++;
    }

    private void publishAndSubscribe() throws RTIexception, RTIinternalError {
        pacjentHandle = rtiamb.getObjectClassHandle("ObjectRoot.Pacjent");
        idPacjentaHandle = rtiamb.getAttributeHandle( pacjentHandle,"id_pacjenta" );


        wejscieHandle = rtiamb.getInteractionClassHandle( "InteractionRoot.Wejscie_do_przychodni" );
        fedamb.wejscieHandle = wejscieHandle;
        godzinaWejsciaHandle = rtiamb.getParameterHandle(wejscieHandle,"godzina_wejscia");
        fedamb.godzinaWejsciaHandle = godzinaWejsciaHandle;
        idPacjentaWejscieHandle = rtiamb.getParameterHandle(wejscieHandle,"id_pacjenta");
        fedamb.idPacjentaWejscieHandle = idPacjentaWejscieHandle;
        rtiamb.publishInteractionClass(wejscieHandle);
    }

    private void advanceTime( double timestep ) throws RTIexception
    {
        fedamb.isAdvancing = true;
        HLAfloat64Time time = timeFactory.makeTime( fedamb.federateTime + timestep );
        rtiamb.timeAdvanceRequest( time );

        while( fedamb.isAdvancing )
        {
            rtiamb.evokeMultipleCallbacks( 0.1, 0.2 );
        }
    }

    private double randomTime() {
        Random r = new Random();
        return 1 +(4 * r.nextDouble());
    }

    private void log( String message )
    {
        System.out.println( "PacjentFederate   : " + message );
    }

    public static void main(String[] args) {
        try {
            new PacjentFederate().runFederate("PacjentFederate");
        } catch (RTIexception | MalformedURLException | RTIinternalError rtIexception) {
            rtIexception.printStackTrace();
        }
    }
}

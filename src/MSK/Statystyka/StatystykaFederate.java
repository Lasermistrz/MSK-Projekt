package MSK.Statystyka;

import MSK.Pacjent.PacjentAmbassador;
import hla.rti1516e.*;
import hla.rti1516e.RtiFactoryFactory;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.exceptions.FederationExecutionAlreadyExists;
import hla.rti1516e.exceptions.RTIexception;
import hla.rti1516e.exceptions.RTIinternalError;
import hla.rti1516e.time.HLAfloat64Interval;
import hla.rti1516e.time.HLAfloat64Time;
import hla.rti1516e.time.HLAfloat64TimeFactory;
import org.portico.impl.hla13.types.DoubleTime;
import org.portico.impl.hla13.types.DoubleTimeInterval;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

public class StatystykaFederate {
    public static final String READY_TO_RUN = "ReadyToRun";
    private RTIambassador rtiamb;
    private StatystykaAmbassador fedamb;
    private final double timeStep = 1.0;
    public static final int ITERATIONS =800;
    private HLAfloat64TimeFactory timeFactory; // set when we join
    protected EncoderFactory encoderFactory;     // set when we join
    protected static InteractionClassHandle przeniesieniePacjentaHandle;
    protected static ParameterHandle idPacjentaPrzeniesienieHandle;
    protected static ParameterHandle miejsceKoncoweHandle;
    protected static InteractionClassHandle wejscieDoLekarza;
    protected static ParameterHandle idPacjentaLekarzHandle;
    protected static ParameterHandle godzinaWejsciaDoLekarzaHandle;
    protected static InteractionClassHandle wejscieDoPrzychodni;
    protected static ParameterHandle idPacjentaPrzychodniaHandle;
    protected static ParameterHandle godzinaWejsciaHandle;


    public void runFederate(String federateName) throws RTIexception, RTIinternalError, MalformedURLException {
        rtiamb = hla.rti1516e.RtiFactoryFactory.getRtiFactory().getRtiAmbassador();
        encoderFactory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();

        log( "Connecting..." );
        fedamb = new StatystykaAmbassador( this );
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


        //while (fedamb.running)
        for( int i = 0; i < ITERATIONS; i++ )
        {
            advanceTime(timeStep);
            rtiamb.evokeMultipleCallbacks( 0.1, 0.2 );
        }

        log("Sredni czas w przychodni " + StatystykaAmbassador.listaPacjentow.GetAverageTimeInClinic());
        log("Sredni czas w poczekalni " + StatystykaAmbassador.listaPacjentow.GetAverageTimeInWaitingRoom());
        log("Sredni czas w rejestracji " + StatystykaAmbassador.listaPacjentow.GetAverageTimeInRegistration());
        log("Sredni czas w gabinecie " + StatystykaAmbassador.listaPacjentow.GetAverageTimeInConsultingRoom());
        log("Wyjscie ostatniego pacjenta " + StatystykaAmbassador.listaPacjentow.GetTimeLastPatient());
        log("Wyjscie ostatniego pacjenta " + (StatystykaAmbassador.listaPacjentow.GetTimeLastPatient()-480.0) + " po zamknięciu rejestracji w przychodni");

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

    private void publishAndSubscribe() throws RTIexception {
        wejscieDoPrzychodni = rtiamb.getInteractionClassHandle( "InteractionRoot.Wejscie_do_przychodni" );
        fedamb.wejscieDoPrzychodniHandle = wejscieDoPrzychodni;
        idPacjentaPrzychodniaHandle = rtiamb.getParameterHandle(wejscieDoPrzychodni,"id_pacjenta");
        godzinaWejsciaHandle = rtiamb.getParameterHandle(wejscieDoPrzychodni,"godzina_wejscia");
        rtiamb.subscribeInteractionClass( wejscieDoPrzychodni );

        przeniesieniePacjentaHandle = rtiamb.getInteractionClassHandle( "InteractionRoot.Przeniesienie_pacjenta" );
        fedamb.przeniesieniePacjentaHandle = przeniesieniePacjentaHandle;
        idPacjentaPrzeniesienieHandle = rtiamb.getParameterHandle(przeniesieniePacjentaHandle,"id_pacjenta");
        miejsceKoncoweHandle = rtiamb.getParameterHandle(przeniesieniePacjentaHandle,"miejsce_koncowe");
        rtiamb.subscribeInteractionClass( przeniesieniePacjentaHandle );

        wejscieDoLekarza = rtiamb.getInteractionClassHandle( "InteractionRoot.Wejscie_do_lekarza" );
        fedamb.wejscieDoLekarzaHandle = wejscieDoLekarza;
        idPacjentaLekarzHandle = rtiamb.getParameterHandle(wejscieDoLekarza,"id_pacjenta");
        godzinaWejsciaDoLekarzaHandle = rtiamb.getParameterHandle(wejscieDoLekarza,"godzina_wejscia_do_lekarza");
        rtiamb.subscribeInteractionClass( wejscieDoLekarza );

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
    private void log( String message )
    {
        System.out.println( "StatystykaFederate   : " + message );
    }

    public static void main(String[] args) {
        try {
            new StatystykaFederate().runFederate("StatystykaFederate");
        } catch (RTIexception | MalformedURLException rtIexception) {
            rtIexception.printStackTrace();
        }
    }
}

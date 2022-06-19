package MSK.GUI;

import hla.rti1516e.*;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.exceptions.*;
import hla.rti1516e.time.HLAfloat64Interval;
import hla.rti1516e.time.HLAfloat64Time;
import hla.rti1516e.time.HLAfloat64TimeFactory;
import hla.rti1516e.time.HLAinteger64Time;
import org.portico.impl.hla13.types.DoubleTime;
import org.portico.impl.hla13.types.DoubleTimeInterval;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class MainFederate {
    public static final int ITERATIONS =800;
    public static final String READY_TO_RUN = "ReadyToRun";
    private final double timeStep = 1.0;


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

    private RTIambassador rtiamb;
    private MainFederateAmbassador fedamb;
    /////////////////////////////////    GUI    ////////////////////////////////////
    private JFrame frame;
    private JPanel mainPanel;

    private JLabel textTimeSimulation;
    private double timeSimulation;
    private JLabel textEndTimeSimulation;
    private double endTimeSimulation;
    private JLabel textPacjentInRejestraction;
    private JLabel textDoctors;
    private JLabel textGabinets;
    private double pacjentInRejestraction;
    private JLabel textPacjentInWaitingRoom;
    private double pacjentInWaitingRoom;



    private void log( String message )
    {
        System.out.println( "ExampleFederate   : " + message );
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
    public void runFederate( String federateName ) throws RTIexception, hla.rti1516e.exceptions.RTIinternalError, CallNotAllowedFromWithinCallback, AlreadyConnected, InvalidLocalSettingsDesignator, ConnectionFailed, UnsupportedCallbackModel, MalformedURLException {
        frame = new JFrame();
        frame.setTitle("MSK");
        mainPanel = new JPanel();

        JPanel upPanel = new JPanel();
        JPanel downPanel = new JPanel();

        timeSimulation = 0;
        endTimeSimulation = 0;
        pacjentInRejestraction = 0;
        pacjentInWaitingRoom = 0;


        //up panel
        JLabel labelTimeSimulation = new JLabel("Aktualny czas symulacji:");
        textTimeSimulation = new JLabel("0");

        JLabel labelEndTimeSimulation = new JLabel("Czas zakończenia symulacji:");
        textEndTimeSimulation = new JLabel("0");

        //center panel
        JLabel labelDoctors = new JLabel("Dostępni lekarze:");
        textDoctors = new JLabel("0");

        JLabel labelConsultingRoom = new JLabel("Dostępne gabinety:");
        textGabinets = new JLabel("0");

        //downPanel
        JLabel labelPacjentInRejestraction = new JLabel("Ilość pacjentów w rejestracji:");
        textPacjentInRejestraction = new JLabel("0");

        JLabel labelPacjentInWaitingRoom = new JLabel("Ilość pacjentów w poczekalni:");
        textPacjentInWaitingRoom = new JLabel("0");


        upPanel.setLayout(new GridLayout(2, 4, 10, 10));
        upPanel.setBounds(10, 5, 800, 90);
        upPanel.add(labelTimeSimulation);
        upPanel.add(textTimeSimulation);
        upPanel.add(labelEndTimeSimulation);
        upPanel.add(textEndTimeSimulation);
        upPanel.add(labelDoctors);
        upPanel.add(textDoctors);
        upPanel.add(labelConsultingRoom);
        upPanel.add(textGabinets);


        downPanel.setLayout(new GridLayout(1, 4, 10, 10));
        downPanel.setBounds(10, 90, 800, 90);
        downPanel.add(labelPacjentInRejestraction);
        downPanel.add(textPacjentInRejestraction);
        downPanel.add(labelPacjentInWaitingRoom);
        downPanel.add(textPacjentInWaitingRoom);


        mainPanel.add(upPanel);
        mainPanel.add(downPanel);
        mainPanel.setLayout(null);


        frame.add(mainPanel);
        frame.setSize(830, 270);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
        frame.setVisible(true);


        frame.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                System.exit(0);
            }
        });

        rtiamb = hla.rti1516e.RtiFactoryFactory.getRtiFactory().getRtiAmbassador();
        encoderFactory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();

        log( "Connecting..." );
        fedamb = new MainFederateAmbassador( this );
        rtiamb.connect( fedamb, CallbackModel.HLA_EVOKED );

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
        while(!fedamb.isReadyToRun)
        {
            rtiamb.evokeMultipleCallbacks( 0.1, 0.2 );
        }
        log( "Achieved sync point: " +READY_TO_RUN+ ", waiting for federation..." );
        enableTimePolicy();
        log( "Time Policy Enabled" );

        publishAndSubscribe();
        log( "Published and Subscribed" );

        /*int objectHandle = registerObject();
        log( "Registered Object, handle=" + objectHandle );*/

        		for( int i = 0; i < ITERATIONS; i++ )
        //while(fedamb.running)
        {
            // 9.1 update the attribute values of the instance //
            //updateAttributeValues( objectHandle );

            // 9.2 send an interaction
            //sendInteraction();


            // 9.3 request a time advance and wait until we get it
            //double timeToAdvance = fedamb.federateTime + timeStep;

            advanceTime(timeStep);
            log( "Time Advanced to " + fedamb.federateTime );
            SetTimeSimulation(fedamb.federateTime);
            SetEndTimeSimulation(MainFederateAmbassador.zakonczeniaCzas);
            SetPacjentInRejestraction(MainFederateAmbassador.iloscPacjentowWRejestracji);
            SetPacjentInWaitingRoom(MainFederateAmbassador.iloscPacjentowWPoczekalni);
            setTextDoctors(MainFederateAmbassador.dostepniLekarze);
            setTextGabinets(MainFederateAmbassador.dostepneGabinety);

            rtiamb.evokeMultipleCallbacks( 0.1, 0.2 );


        }
        log("===================== KONIEC SYMULACJI ===================== ");

        log("Czas zakonczenia = " + MainFederateAmbassador.zakonczeniaCzas);
        log("Czas dostepni lekarze = " + MainFederateAmbassador.dostepniLekarze);
        log("Czas dostepne gabinety = " + MainFederateAmbassador.dostepneGabinety);
        log("Czas ilosc pacjentow w rejestracji = " + MainFederateAmbassador.iloscPacjentowWRejestracji);
        log("Czas ilosc pacjentow w poczekalni = " + MainFederateAmbassador.iloscPacjentowWPoczekalni);


        /*deleteObject( objectHandle );
        log( "Deleted Object, handle=" + objectHandle );*/

        rtiamb.resignFederationExecution( ResignAction.NO_ACTION );
        log( "Resigned from Federation" );

        try
        {
            rtiamb.destroyFederationExecution( "ExampleFederation" );
            log( "Destroyed Federation" );
        }
        catch( FederationExecutionDoesNotExist dne )
        {
            log( "No need to destroy federation, it doesn't exist" );
        }
        catch( FederatesCurrentlyJoined fcj )
        {
            log( "Didn't destroy federation, federates still joined" );
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
    private void advanceTime( double timeToAdvance ) throws RTIexception {
        fedamb.isAdvancing = true;
        HLAfloat64Time time = timeFactory.makeTime( fedamb.federateTime + timeToAdvance );
        rtiamb.timeAdvanceRequest( time );

        while( fedamb.isAdvancing )
        {
            rtiamb.evokeMultipleCallbacks( 0.1, 0.2 );
        }
    }


    private double getLbts()
    {
        return fedamb.federateTime + fedamb.federateLookahead;
    }

    private byte[] generateTag()
    {
        return (""+System.currentTimeMillis()).getBytes();
    }

    public static void main( String[] args )
    {
        // get a federate name, use "exampleFederate" as default
        String federateName = "exampleFederate";
        if( args.length != 0 )
        {
            federateName = args[0];

        }

        try
        {
            new MainFederate().runFederate( federateName );
        }
        catch(RTIexception | MalformedURLException rtie )
        {
            rtie.printStackTrace();
        }
    }

    public void SetTimeSimulation(double time){
        textTimeSimulation.setText(String.format("%.2f", time));
    }

    public void SetEndTimeSimulation(double time){
        textEndTimeSimulation.setText(String.format("%.2f", time));
    }

    public void SetPacjentInRejestraction(int time){
        textPacjentInRejestraction.setText(String.valueOf(time));
    }

    public void SetPacjentInWaitingRoom(int time){
        textPacjentInWaitingRoom.setText(String.valueOf(time));
    }

    public void setTextDoctors(int number){
        textDoctors.setText(String.valueOf(number));
    }
    public void setTextGabinets(int number){
        textGabinets.setText(String.valueOf(number));
    }

}



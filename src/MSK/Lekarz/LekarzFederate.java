package MSK.Lekarz;

import MSK.Rejestracja.RejestracjaAmbassador;
import hla.rti.jlc.EncodingHelpers;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAinteger32BE;
import hla.rti1516e.exceptions.FederationExecutionAlreadyExists;
import hla.rti1516e.exceptions.RTIexception;
import hla.rti1516e.exceptions.RTIinternalError;
import hla.rti1516e.time.HLAfloat64Interval;
import hla.rti1516e.time.HLAfloat64Time;
import hla.rti1516e.time.HLAfloat64TimeFactory;
import hla.rti1516e.*;
import org.portico.impl.hla13.types.DoubleTimeInterval;
import org.portico.impl.hla1516e.types.time.DoubleTime;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

public class LekarzFederate {
    public static final String READY_TO_RUN = "ReadyToRun";
    private RTIambassador rtiamb;
    private LekarzAmbassador fedamb;
    private final double timeStep = 1.0;
    Random r = new Random();
    private HLAfloat64TimeFactory timeFactory; // set when we join
    protected EncoderFactory encoderFactory;     // set when we join
    protected static InteractionClassHandle przeniesieniePacjentaHandle;
    protected static ParameterHandle idPacjentaPrzeniesienieHandle;
    protected static ParameterHandle miejsceKoncoweHandle;
    protected static InteractionClassHandle wejscieDoLekarza;
    protected static ParameterHandle idPacjentaLekarzHandle;
    protected static ParameterHandle godzinaWejsciaDoLekarzaHandle;
    /***
     * @value
     * 3- wszedł do Gabinetu
     * 4- nie wszedł do Gabinetu
     */
    protected int czyWszedlDoGabinetu;


    public void runFederate(String federateName) throws RTIexception, RTIinternalError, MalformedURLException {
        rtiamb = RtiFactoryFactory.getRtiFactory().getRtiAmbassador();
        encoderFactory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();

        log( "Connecting..." );
        fedamb = new LekarzAmbassador( this );
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
        while( fedamb.isReadyToRun == false )
        {
            rtiamb.evokeMultipleCallbacks( 0.1, 0.2 );
        }

        enableTimePolicy();

        publishAndSubscribe();


        while (fedamb.running) {
            advanceTime(2*randomTime());

            if(LekarzAmbassador.lista.size()>0&&LekarzAmbassador.iloscWolnychGabinetow>0){
                    czyWszedlDoGabinetu = sendInteraction(fedamb.federateTime + fedamb.federateLookahead,LekarzAmbassador.lista.get(0));
                    if(czyWszedlDoGabinetu==3) {
                        log("Pacjent nr " + LekarzAmbassador.lista.get(0) + " uda sie do gabinetu");
                        LekarzAmbassador.iloscWolnychGabinetow--;
                    }
                    else log("Pacjent nr " + LekarzAmbassador.lista.get(0) + " zostal obsluzony");
                    LekarzAmbassador.lista.remove(0);
            }
            rtiamb.evokeMultipleCallbacks( 0.1, 0.2 );
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

    private int sendInteraction(double timeStep,int id_pacjenta) throws RTIexception {
        HLAfloat64Time time = timeFactory.makeTime(fedamb.federateTime + fedamb.federateLookahead);

        ParameterHandleValueMap parameters = rtiamb.getParameterHandleValueMapFactory().create(0);
        HLAinteger32BE idPacjenta = encoderFactory.createHLAinteger32BE(id_pacjenta);
        HLAinteger32BE miejsce_koncowe ;
        int temp;
        if(r.nextDouble()>0.6){
            temp=3;
        }else{
            temp=4;
        }
        miejsce_koncowe = encoderFactory.createHLAinteger32BE(temp);

        parameters.put(idPacjentaPrzeniesienieHandle,idPacjenta.toByteArray());
        parameters.put(miejsceKoncoweHandle,miejsce_koncowe.toByteArray());
        rtiamb.sendInteraction(przeniesieniePacjentaHandle,parameters,"tag".getBytes(),time);

        return temp;
    }

    private void publishAndSubscribe() throws RTIexception {

        przeniesieniePacjentaHandle = rtiamb.getInteractionClassHandle( "InteractionRoot.Przeniesienie_pacjenta" );
        fedamb.przeniesieniePacjentaHandle = przeniesieniePacjentaHandle;
        idPacjentaPrzeniesienieHandle = rtiamb.getParameterHandle(przeniesieniePacjentaHandle,"id_pacjenta");
        miejsceKoncoweHandle = rtiamb.getParameterHandle(przeniesieniePacjentaHandle,"miejsce_koncowe");
        rtiamb.subscribeInteractionClass( przeniesieniePacjentaHandle );
        rtiamb.publishInteractionClass(przeniesieniePacjentaHandle);

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

    private double randomTime() {
        Random r = new Random();
        return 1 +(4 * r.nextDouble());
    }


    private void log( String message )
    {
        System.out.println( "LekarzFederate   : " + message );
    }

    public static void main(String[] args) {
        try {
            new LekarzFederate().runFederate("LekarzFederate");
        } catch (RTIexception | MalformedURLException rtIexception) {
            rtIexception.printStackTrace();
        }
    }
}

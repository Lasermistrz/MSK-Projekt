package MSK.Lekarz;

import MSK.Poczekalnia.PoczekalniaAmbassador;
import hla.rti.*;
import hla.rti.jlc.EncodingHelpers;
import hla.rti.jlc.RtiFactoryFactory;
import org.portico.impl.hla13.types.DoubleTime;
import org.portico.impl.hla13.types.DoubleTimeInterval;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.nio.ByteBuffer;
import java.util.Random;

public class LekarzFederate {
    public static final String READY_TO_RUN = "ReadyToRun";
    private RTIambassador rtiamb;
    private LekarzAmbassador fedamb;
    private final double timeStep = 10.0;
    Random r = new Random();
    /***
     * @value
     * 3- wszedł do Gabinetu
     * 4- nie wszedł do Gabinetu
     */
    protected int czyWszedlDoGabinetu;


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

        fedamb = new LekarzAmbassador();
        rtiamb.joinFederationExecution( "LekarzFederate", "ExampleFederation", fedamb );
        log( "Joined Federation as LekarzFederate");

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

    private int sendInteraction(double timeStep,int id_pacjenta) throws RTIexception {

        LogicalTime time = convertTime(timeStep);
        SuppliedParameters parameters = RtiFactoryFactory.getRtiFactory().createSuppliedParameters();
        int przeniesienieHandle = rtiamb.getInteractionClassHandle( "InteractionRoot.Przeniesienie_pacjenta" );
        int idPacjentaHandlePar = rtiamb.getParameterHandle("id_pacjenta",przeniesienieHandle);
        int miejsceHandlePar = rtiamb.getParameterHandle("miejsce_koncowe",przeniesienieHandle);
        byte[] idPacjenta = EncodingHelpers.encodeInt(id_pacjenta);
        byte[] miejsce_koncowe;
        int temp;
        if(r.nextDouble()>0.6){
            temp=3;
            miejsce_koncowe = EncodingHelpers.encodeInt(temp);
        }else{
            temp=4;
            miejsce_koncowe = EncodingHelpers.encodeInt(temp);
        }

        parameters.add(idPacjentaHandlePar,idPacjenta);
        parameters.add(miejsceHandlePar,miejsce_koncowe);
        rtiamb.sendInteraction(przeniesienieHandle,parameters,"tag".getBytes(),time);

        return temp;
    }

    private void publishAndSubscribe() throws RTIexception {

        int wejscieDoLekarzaHandle = rtiamb.getInteractionClassHandle( "InteractionRoot.Wejscie_do_lekarza" );
        fedamb.wejscieDoLekarzaHlaHandle = wejscieDoLekarzaHandle;
        rtiamb.subscribeInteractionClass(wejscieDoLekarzaHandle);

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
        System.out.println( "LekarzFederate   : " + message );
    }

    public static void main(String[] args) {
        try {
            new LekarzFederate().runFederate();
        } catch (RTIexception rtIexception) {
            rtIexception.printStackTrace();
        }
    }
}

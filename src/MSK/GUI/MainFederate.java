package MSK.GUI;

import hla.rti.*;
import hla.rti.jlc.RtiFactoryFactory;
import org.portico.impl.hla13.types.DoubleTime;
import org.portico.impl.hla13.types.DoubleTimeInterval;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.MalformedURLException;

public class MainFederate {
    public static final int ITERATIONS = 200;
    public static final String READY_TO_RUN = "ReadyToRun";
    private final double timeStep = 1.0;

    private RTIambassador rtiamb;
    private MainFederateAmbassador fedamb;

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

    private LogicalTime convertTime(double time )
    {
        // PORTICO SPECIFIC!!
        return new DoubleTime( time );
    }

    private LogicalTimeInterval convertInterval(double time )
    {
        // PORTICO SPECIFIC!!
        return new DoubleTimeInterval( time );
    }

    public void runFederate( String federateName ) throws RTIexception
    {
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

        fedamb = new MainFederateAmbassador();
        rtiamb.joinFederationExecution( federateName, "ExampleFederation", fedamb);
        log( "Joined Federation as " + federateName );

        rtiamb.registerFederationSynchronizationPoint( READY_TO_RUN, null );

        while(!fedamb.isAnnounced)
        {
            rtiamb.tick();
        }

        waitForUser();

        rtiamb.synchronizationPointAchieved( READY_TO_RUN );
        log( "Achieved sync point: " +READY_TO_RUN+ ", waiting for federation..." );
        while(!fedamb.isReadyToRun)
        {
            rtiamb.tick();
        }

        enableTimePolicy();
        log( "Time Policy Enabled" );

        publishAndSubscribe();
        log( "Published and Subscribed" );

        /*int objectHandle = registerObject();
        log( "Registered Object, handle=" + objectHandle );*/

        //		for( int i = 0; i < ITERATIONS; i++ )
        while(fedamb.running)
        {
            // 9.1 update the attribute values of the instance //
            //updateAttributeValues( objectHandle );

            // 9.2 send an interaction
            //sendInteraction();


            // 9.3 request a time advance and wait until we get it
            double timeToAdvance = fedamb.federateTime + timeStep;
            advanceTime(timeToAdvance);
            log( "Time Advanced to " + fedamb.federateTime );

            rtiamb.tick();

        }
        log("===================== KONIEC SYMULACJI ===================== ");

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
        int classHandle = rtiamb.getObjectClassHandle("ObjectRoot.Pacjent");
        int idHandle = rtiamb.getAttributeHandle("id_pacjenta",classHandle);

        AttributeHandleSet attributes = RtiFactoryFactory.getRtiFactory().createAttributeHandleSet();
        attributes.add( idHandle );

        rtiamb.subscribeObjectClassAttributes(classHandle,attributes);

        int wejscieHandle = rtiamb.getInteractionClassHandle( "InteractionRoot.Wejscie_do_przychodni" );
        fedamb.wejscieDoPrzychodniHandle = wejscieHandle;
        rtiamb.subscribeInteractionClass( wejscieHandle );
    }

    private void enableTimePolicy() throws RTIexception
    {
        LogicalTime currentTime = convertTime( fedamb.federateTime );
        LogicalTimeInterval lookahead = convertInterval( fedamb.federateLookahead );


        this.rtiamb.enableTimeRegulation( currentTime, lookahead );

        while(!fedamb.isRegulating)
        {
            rtiamb.tick();
        }

        this.rtiamb.enableTimeConstrained();

        while(!fedamb.isConstrained)
        {
            rtiamb.tick();
        }
    }
    private void advanceTime( double timeToAdvance ) throws RTIexception {
        fedamb.isAdvancing = true;
        LogicalTime newTime = convertTime( timeToAdvance );
        rtiamb.timeAdvanceRequest( newTime );

        while( fedamb.isAdvancing )
        {
            rtiamb.tick();
        }
    }

    private void deleteObject( int handle ) throws RTIexception
    {
        rtiamb.deleteObjectInstance( handle, generateTag() );
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
        catch( RTIexception rtie )
        {
            rtie.printStackTrace();
        }
    }

}



package MSK.Rejestracja;


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

public class RejestracjaFederate {
    public static final String READY_TO_RUN = "ReadyToRun";
    protected int rejestracjaHlaHandle;
    private RTIambassador rtiamb;
    private RejestracjaAmbassador fedamb;
    public static int iloscWolnychMiejscWPoczekalni = 30;
    private final double timeStep = 10.0;


    public void runFederate() throws RTIexception {
        rtiamb = RtiFactoryFactory.getRtiFactory().createRtiAmbassador();

        try {
            File fom = new File("msk.fed");
            rtiamb.createFederationExecution("ExampleFederation",
                    fom.toURI().toURL());
            log("Created Federation");
        } catch (FederationExecutionAlreadyExists exists) {
            log("Didn't create federation, it already existed");
        } catch (MalformedURLException urle) {
            log("Exception processing fom: " + urle.getMessage());
            urle.printStackTrace();
            return;
        }

        fedamb = new RejestracjaAmbassador();
        rtiamb.joinFederationExecution("RejestracjaFederate", "ExampleFederation", fedamb);
        log("Joined Federation as RejestracjaFederate");

        rtiamb.registerFederationSynchronizationPoint(READY_TO_RUN, null);

        while (fedamb.isAnnounced == false) {
            rtiamb.tick();
        }

        waitForUser();

        rtiamb.synchronizationPointAchieved(READY_TO_RUN);
        log("Achieved sync point: " + READY_TO_RUN + ", waiting for federation...");
        while (fedamb.isReadyToRun == false) {
            rtiamb.tick();
        }

        enableTimePolicy();

        publishAndSubscribe();

        registerRejestracjaObject();

        while (fedamb.running) {
            advanceTime(randomTime());

            //updateHLAObject(fedamb.federateTime + fedamb.federateLookahead);
            if (iloscWolnychMiejscWPoczekalni > 0 && RejestracjaAmbassador.lista.size() > 0) {
                log(RejestracjaAmbassador.lista.get(0) + " pacjent");
                sendInteraction(fedamb.federateTime + fedamb.federateLookahead, RejestracjaAmbassador.lista.get(0));
                RejestracjaAmbassador.lista.remove(0);
                iloscWolnychMiejscWPoczekalni--;
            }

            rtiamb.tick();
        }

    }

    private void registerRejestracjaObject() throws RTIexception {
        int classHandleCreate = rtiamb.getObjectClassHandle("ObjectRoot.Rejestracja");
        this.rejestracjaHlaHandle = rtiamb.registerObjectInstance(classHandleCreate);

    }

//    private void updateHLAObject(double time) throws RTIexception{
//        SuppliedAttributes attributes =
//                RtiFactoryFactory.getRtiFactory().createSuppliedAttributes();
//        for (int i: this.pacjentHlaHandle) {
//            int classHandle = rtiamb.getObjectClass(pacjentHlaHandle[i]);
//            int miejsceHandle = rtiamb.getAttributeHandle( "miejsce", classHandle );
//            byte[] miejscekValue = ByteBuffer.allocate(4).putInt(i).array();
//            //zmiana miejsca pobytu pacjenta
//
//            attributes.add(miejsceHandle, miejscekValue);
//            LogicalTime logicalTime = convertTime( time );
//            rtiamb.updateAttributeValues( pacjentHlaHandle[i], attributes, "actualize".getBytes(), logicalTime );
//        }
//    }

    private void waitForUser() {
        log(" >>>>>>>>>> Press Enter to Continue <<<<<<<<<<");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            reader.readLine();
        } catch (Exception e) {
            log("Error while waiting for user input: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void enableTimePolicy() throws RTIexception {
        LogicalTime currentTime = convertTime(fedamb.federateTime);
        LogicalTimeInterval lookahead = convertInterval(fedamb.federateLookahead);

        this.rtiamb.enableTimeRegulation(currentTime, lookahead);

        while (fedamb.isRegulating == false) {
            rtiamb.tick();
        }

        this.rtiamb.enableTimeConstrained();

        while (fedamb.isConstrained == false) {
            rtiamb.tick();
        }
    }

    private void sendInteraction(double timeStep, int id_pacjenta) throws RTIexception {
        LogicalTime time = convertTime(timeStep);

        SuppliedParameters parameters = RtiFactoryFactory.getRtiFactory().createSuppliedParameters();
        int przeniesienieHandle = rtiamb.getInteractionClassHandle("InteractionRoot.Przeniesienie_pacjenta");
        int idPacjentaHandlePar = rtiamb.getParameterHandle("id_pacjenta", przeniesienieHandle);
        int miejsceHandlePar = rtiamb.getParameterHandle("miejsce_koncowe", przeniesienieHandle);
        byte[] idPacjenta = EncodingHelpers.encodeInt(id_pacjenta);
        byte[] miejsce_koncowe = EncodingHelpers.encodeInt(1);
        parameters.add(idPacjentaHandlePar, idPacjenta);
        parameters.add(miejsceHandlePar, miejsce_koncowe);
        rtiamb.sendInteraction(przeniesienieHandle, parameters, "tag".getBytes(), time);
        log("Przeniesiono pacjenta nr " + id_pacjenta + " do poczekalni");

    }

    private void publishAndSubscribe() throws RTIexception {
        int classHandle = rtiamb.getObjectClassHandle("ObjectRoot.Rejestracja");
        int listaHandle = rtiamb.getAttributeHandle("lista", classHandle);

        AttributeHandleSet attributes = RtiFactoryFactory.getRtiFactory().createAttributeHandleSet();
        attributes.add(listaHandle);

        rtiamb.publishObjectClass(classHandle, attributes);


        int przeniesienieHandle = rtiamb.getInteractionClassHandle("InteractionRoot.Przeniesienie_pacjenta");
        fedamb.przeniesienieHlaHandle = przeniesienieHandle;
        rtiamb.publishInteractionClass(przeniesienieHandle);
        rtiamb.subscribeInteractionClass(przeniesienieHandle);

        int wejscieDoLekarzaHandle = rtiamb.getInteractionClassHandle("InteractionRoot.Wejscie_do_lekarza");
        fedamb.wejscieDoLekarzaHlaHandle = wejscieDoLekarzaHandle;
        rtiamb.subscribeInteractionClass(wejscieDoLekarzaHandle);

        int wejscieHandle = rtiamb.getInteractionClassHandle("InteractionRoot.Wejscie_do_przychodni");
        fedamb.wejscieDoPrzychodniHandle = wejscieHandle;
        rtiamb.subscribeInteractionClass(wejscieHandle);
    }

    private void advanceTime(double timestep) throws RTIexception {
        //log("requesting time advance for: " + timestep);

        // request the advance
        fedamb.isAdvancing = true;
        LogicalTime newTime = convertTime(fedamb.federateTime + timestep);
        rtiamb.timeAdvanceRequest(newTime);
        while (fedamb.isAdvancing) {
            rtiamb.tick();
        }
        fedamb.federateTime += timestep;
    }

    private double randomTime() {
        Random r = new Random();
        return 1 + (4 * r.nextDouble());
    }

    private LogicalTime convertTime(double time) {
        // PORTICO SPECIFIC!!
        return new DoubleTime(time);
    }

    /**
     * Same as for {@link #convertTime(double)}
     */
    private LogicalTimeInterval convertInterval(double time) {
        // PORTICO SPECIFIC!!
        return new DoubleTimeInterval(time);
    }

    private void log(String message) {
        System.out.println("RejestracjaFederate   : " + message);
    }

    public static void main(String[] args) {
        try {
            new RejestracjaFederate().runFederate();
        } catch (RTIexception rtIexception) {
            rtIexception.printStackTrace();
        }
    }
}

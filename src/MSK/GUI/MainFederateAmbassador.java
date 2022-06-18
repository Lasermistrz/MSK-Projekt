package MSK.GUI;

import MSK.Parameters;
import hla.rti.*;
import hla.rti.jlc.EncodingHelpers;
import hla.rti1516.jlc.NullFederateAmbassador;
import org.portico.impl.hla13.types.DoubleTime;

public class MainFederateAmbassador extends NullFederateAmbassador implements FederateAmbassador {
    protected double federateTime = 0.0;
    protected double federateLookahead = 1.0;

    protected boolean isRegulating = false;
    protected boolean isConstrained = false;
    protected boolean isAdvancing = false;

    protected boolean isAnnounced = false;
    protected boolean isReadyToRun = false;

    protected boolean running = true;
    protected int wejscieDoPrzychodniHandle;
    protected int przeniesieniePacjentaHandle;
    protected int wejscieDoLekarzaHandle;

    /////////////////////////    Zmienne do GUI    ////////////////////////////
    public static double aktualnyCzas = 0.0;
    public static double zakonczeniaCzas = 0.0;
    public static int dostepniLekarze = Parameters.iloscLekarzy;
    public static int dostepneGabinety = Parameters.iloscGabinetow;
    public static int iloscPacjentowWRejestracji = 0;
    public static int iloscPacjentowWPoczekalni = 0;

    public MainFederateAmbassador() {
    }

    private double convertTime(LogicalTime logicalTime) {
        // PORTICO SPECIFIC!!
        return ((DoubleTime) logicalTime).getTime();
    }

    private void log(String message) {
        System.out.println("FederateAmbassador: " + message);
    }

    public void synchronizationPointRegistrationFailed(String label) {
        log("Failed to register sync point: " + label);
    }

    public void synchronizationPointRegistrationSucceeded(String label) {
        log("Successfully registered sync point: " + label);
    }

    public void announceSynchronizationPoint(String label, byte[] tag) {
        log("Synchronization point announced: " + label);
        if (label.equals(MainFederate.READY_TO_RUN))
            this.isAnnounced = true;
    }

    public void federationSynchronized(String label) {
        log("Federation Synchronized: " + label);
        if (label.equals(MainFederate.READY_TO_RUN))
            this.isReadyToRun = true;
    }

    @Override
    public void initiateFederateSave(String s) {

    }

    @Override
    public void federationSaved() {

    }

    @Override
    public void federationNotSaved() throws FederateInternalError {

    }

    @Override
    public void requestFederationRestoreSucceeded(String s) {

    }

    @Override
    public void requestFederationRestoreFailed(String s, String s1) throws FederateInternalError {

    }

    @Override
    public void federationRestoreBegun() {

    }

    @Override
    public void initiateFederateRestore(String s, int i) throws SpecifiedSaveLabelDoesNotExist, CouldNotRestore, FederateInternalError {

    }

    @Override
    public void federationRestored() {

    }

    @Override
    public void federationNotRestored() throws FederateInternalError {

    }

    @Override
    public void startRegistrationForObjectClass(int i) throws ObjectClassNotPublished, FederateInternalError {

    }

    @Override
    public void stopRegistrationForObjectClass(int i) throws ObjectClassNotPublished, FederateInternalError {

    }

    @Override
    public void turnInteractionsOn(int i) throws InteractionClassNotPublished, FederateInternalError {

    }

    @Override
    public void turnInteractionsOff(int i) throws InteractionClassNotPublished, FederateInternalError {

    }

    public void timeRegulationEnabled(LogicalTime theFederateTime) {
        this.federateTime = convertTime(theFederateTime);
        this.isRegulating = true;
    }

    public void timeConstrainedEnabled(LogicalTime theFederateTime) {
        this.federateTime = convertTime(theFederateTime);
        this.isConstrained = true;
    }

    public void timeAdvanceGrant(LogicalTime theTime) {
        this.federateTime = convertTime(theTime);
        this.isAdvancing = false;
    }

    @Override
    public void requestRetraction(EventRetractionHandle eventRetractionHandle) throws EventNotKnown, FederateInternalError {

    }

    public void discoverObjectInstance(int theObject, int theObjectClass, String objectName) {
        /*log( "Discoverd Object: handle=" + theObject + ", classHandle=" +
                theObjectClass + ", name=" + objectName );*/
    }

    @Override
    public void reflectAttributeValues(int i, ReflectedAttributes reflectedAttributes, byte[] bytes) throws ObjectNotKnown, AttributeNotKnown, FederateOwnsAttributes, FederateInternalError {

    }

    public void reflectAttributeValues(int theObject, ReflectedAttributes theAttributes, byte[] tag, LogicalTime theTime, EventRetractionHandle retractionHandle) {
    }

    public void receiveInteraction(int interactionClass, ReceivedInteraction theInteraction, byte[] tag) {
        receiveInteraction(interactionClass, theInteraction, tag, null, null);
    }

    public void receiveInteraction(int interactionClass, ReceivedInteraction theInteraction, byte[] tag, LogicalTime theTime, EventRetractionHandle eventRetractionHandle) {
        StringBuilder builder = new StringBuilder("Interaction Received:");
        try {
            if (interactionClass == wejscieDoPrzychodniHandle) {
                int id_pacjenta = EncodingHelpers.decodeInt(theInteraction.getValue(0));
                double godzina_wejscia = EncodingHelpers.decodeDouble(theInteraction.getValue(1));
                builder.append("Przybyl pacjent nr " + id_pacjenta + " do Przychodni, time=" + godzina_wejscia);
                MainFederateAmbassador.iloscPacjentowWRejestracji++;
                MainFederateAmbassador.zakonczeniaCzas=convertTime(theTime);
            } else if (interactionClass == wejscieDoLekarzaHandle) {
                int id_pacjenta = EncodingHelpers.decodeInt(theInteraction.getValue(0));
                double godzina_wejscia = EncodingHelpers.decodeDouble(theInteraction.getValue(1));
                builder.append("Przybyl pacjent nr " + id_pacjenta + " do Lekarza, time=" + godzina_wejscia);
                MainFederateAmbassador.dostepniLekarze--;
                MainFederateAmbassador.iloscPacjentowWPoczekalni--;
                MainFederateAmbassador.zakonczeniaCzas=convertTime(theTime);
            } else if (interactionClass == przeniesieniePacjentaHandle && EncodingHelpers.decodeInt(theInteraction.getValue(1)) == 1) {
                int id_pacjenta = EncodingHelpers.decodeInt(theInteraction.getValue(0));
                builder.append("Przybyl pacjent nr " + id_pacjenta + " do Poczekalni");
                MainFederateAmbassador.iloscPacjentowWRejestracji--;
                MainFederateAmbassador.iloscPacjentowWPoczekalni++;
                MainFederateAmbassador.zakonczeniaCzas=convertTime(theTime);
            } else if (interactionClass == przeniesieniePacjentaHandle && EncodingHelpers.decodeInt(theInteraction.getValue(1)) == 3) {
                int id_pacjenta = EncodingHelpers.decodeInt(theInteraction.getValue(0));
                builder.append("Pacjent nr " + id_pacjenta + " przeniesiony do Gabinetu ");
                MainFederateAmbassador.dostepneGabinety--;
                MainFederateAmbassador.dostepniLekarze++;
                MainFederateAmbassador.zakonczeniaCzas=convertTime(theTime);
            } else if (interactionClass == przeniesieniePacjentaHandle && EncodingHelpers.decodeInt(theInteraction.getValue(1)) == 4) {
                int id_pacjenta = EncodingHelpers.decodeInt(theInteraction.getValue(0));
                builder.append("Przybyl pacjent nr " + id_pacjenta + " obsluzony przez lekarza");
                MainFederateAmbassador.dostepniLekarze++;
                MainFederateAmbassador.zakonczeniaCzas=convertTime(theTime);
            } else if (interactionClass == przeniesieniePacjentaHandle && EncodingHelpers.decodeInt(theInteraction.getValue(1)) == 5) {
                int id_pacjenta = EncodingHelpers.decodeInt(theInteraction.getValue(0));
                builder.append("Przybyl pacjent nr " + id_pacjenta + " obsluzony w Gabinecie");
                MainFederateAmbassador.dostepneGabinety++;
                MainFederateAmbassador.zakonczeniaCzas=convertTime(theTime);
            }


        } catch (ArrayIndexOutOfBounds e) {
            throw new RuntimeException(e);
        }
        log(builder.toString());
    }

    public void removeObjectInstance(int theObject, byte[] userSuppliedTag) {
        log("Object Removed: handle=" + theObject);
    }

    public void removeObjectInstance(int theObject, byte[] userSuppliedTag, LogicalTime theTime, EventRetractionHandle retractionHandle) {
        log("Object Removed: handle=" + theObject);
    }

    @Override
    public void attributesInScope(int i, AttributeHandleSet attributeHandleSet) throws ObjectNotKnown, AttributeNotKnown, FederateInternalError {

    }

    @Override
    public void attributesOutOfScope(int i, AttributeHandleSet attributeHandleSet) throws ObjectNotKnown, AttributeNotKnown, FederateInternalError {

    }

    @Override
    public void provideAttributeValueUpdate(int i, AttributeHandleSet attributeHandleSet) throws ObjectNotKnown, AttributeNotKnown, AttributeNotOwned, FederateInternalError {

    }

    @Override
    public void turnUpdatesOnForObjectInstance(int i, AttributeHandleSet attributeHandleSet) throws ObjectNotKnown, AttributeNotOwned, FederateInternalError {

    }

    @Override
    public void turnUpdatesOffForObjectInstance(int i, AttributeHandleSet attributeHandleSet) throws ObjectNotKnown, AttributeNotOwned, FederateInternalError {

    }

    @Override
    public void requestAttributeOwnershipAssumption(int i, AttributeHandleSet attributeHandleSet, byte[] bytes) throws ObjectNotKnown, AttributeNotKnown, AttributeAlreadyOwned, AttributeNotPublished, FederateInternalError {

    }

    @Override
    public void attributeOwnershipDivestitureNotification(int i, AttributeHandleSet attributeHandleSet) throws ObjectNotKnown, AttributeNotKnown, AttributeNotOwned, AttributeDivestitureWasNotRequested, FederateInternalError {

    }

    @Override
    public void attributeOwnershipAcquisitionNotification(int i, AttributeHandleSet attributeHandleSet) throws ObjectNotKnown, AttributeNotKnown, AttributeAcquisitionWasNotRequested, AttributeAlreadyOwned, AttributeNotPublished, FederateInternalError {

    }

    @Override
    public void attributeOwnershipUnavailable(int i, AttributeHandleSet attributeHandleSet) throws ObjectNotKnown, AttributeNotKnown, AttributeAlreadyOwned, AttributeAcquisitionWasNotRequested, FederateInternalError {

    }

    @Override
    public void requestAttributeOwnershipRelease(int i, AttributeHandleSet attributeHandleSet, byte[] bytes) throws ObjectNotKnown, AttributeNotKnown, AttributeNotOwned, FederateInternalError {

    }

    @Override
    public void confirmAttributeOwnershipAcquisitionCancellation(int i, AttributeHandleSet attributeHandleSet) throws ObjectNotKnown, AttributeNotKnown, AttributeAlreadyOwned, AttributeAcquisitionWasNotCanceled, FederateInternalError {

    }

    @Override
    public void informAttributeOwnership(int i, int i1, int i2) throws ObjectNotKnown, AttributeNotKnown, FederateInternalError {

    }

    @Override
    public void attributeIsNotOwned(int i, int i1) throws ObjectNotKnown, AttributeNotKnown, FederateInternalError {

    }

    @Override
    public void attributeOwnedByRTI(int i, int i1) throws ObjectNotKnown, AttributeNotKnown, FederateInternalError {

    }
}

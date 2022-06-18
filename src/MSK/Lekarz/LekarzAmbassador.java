package MSK.Lekarz;

import MSK.GUI.MainFederate;
import MSK.Parameters;
import MSK.Poczekalnia.PoczekalniaAmbassador;
import hla.rti.*;
import hla.rti.jlc.EncodingHelpers;
import org.portico.impl.hla13.types.DoubleTime;

import java.util.ArrayList;

public class LekarzAmbassador implements FederateAmbassador {
    protected double federateTime        = 0.0;
    protected double federateLookahead   = 1.0;

    protected boolean isRegulating       = false;
    protected boolean isConstrained      = false;
    protected boolean isAdvancing        = false;

    protected boolean isAnnounced        = false;
    protected boolean isReadyToRun       = false;
    protected boolean running 			 = true;
    protected int wejscieDoLekarzaHlaHandle;
    protected int przeniesienieHlaHandle;
    public static ArrayList<Integer> lista = new ArrayList<>();
    public static int iloscWolnychGabinetow= Parameters.iloscGabinetow;

    private double convertTime( LogicalTime logicalTime )
    {
        // PORTICO SPECIFIC!!
        return ((DoubleTime)logicalTime).getTime();
    }

    private void log( String message )
    {
        System.out.println( "LekarzAmbassador : " + message );
    }

    public void synchronizationPointRegistrationFailed( String label )
    {
        log( "Failed to register sync point: " + label );
    }

    public void synchronizationPointRegistrationSucceeded( String label )
    {
        log( "Successfully registered sync point: " + label );
    }

    public void announceSynchronizationPoint( String label, byte[] tag )
    {
        log( "Synchronization point announced: " + label );
        if( label.equals(MainFederate.READY_TO_RUN) )
            this.isAnnounced = true;
    }

    public void federationSynchronized( String label )
    {
        log( "Federation Synchronized: " + label );
        if( label.equals(MainFederate.READY_TO_RUN) )
            this.isReadyToRun = true;
    }

    @Override
    public void initiateFederateSave(String s) throws UnableToPerformSave, FederateInternalError {

    }

    @Override
    public void federationSaved() throws FederateInternalError {

    }

    @Override
    public void federationNotSaved() throws FederateInternalError {

    }

    @Override
    public void requestFederationRestoreSucceeded(String s) throws FederateInternalError {

    }

    @Override
    public void requestFederationRestoreFailed(String s, String s1) throws FederateInternalError {

    }

    @Override
    public void federationRestoreBegun() throws FederateInternalError {

    }

    @Override
    public void initiateFederateRestore(String s, int i) throws SpecifiedSaveLabelDoesNotExist, CouldNotRestore, FederateInternalError {

    }

    @Override
    public void federationRestored() throws FederateInternalError {

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

    @Override
    public void discoverObjectInstance(int i, int i1, String s) throws CouldNotDiscover, ObjectClassNotKnown, FederateInternalError {

    }

    @Override
    public void reflectAttributeValues(int i, ReflectedAttributes reflectedAttributes, byte[] bytes) throws ObjectNotKnown, AttributeNotKnown, FederateOwnsAttributes, FederateInternalError {

    }

    @Override
    public void reflectAttributeValues(int i, ReflectedAttributes reflectedAttributes, byte[] bytes, LogicalTime logicalTime, EventRetractionHandle eventRetractionHandle) throws ObjectNotKnown, AttributeNotKnown, FederateOwnsAttributes, InvalidFederationTime, FederateInternalError {

    }

    @Override
    public void receiveInteraction(int i, ReceivedInteraction receivedInteraction, byte[] bytes) throws InteractionClassNotKnown, InteractionParameterNotKnown, FederateInternalError {

    }

    @Override
    public void receiveInteraction(int interactionClass, ReceivedInteraction theInteraction, byte[] tag, LogicalTime theTime, EventRetractionHandle eventRetractionHandle) throws InteractionClassNotKnown, InteractionParameterNotKnown, InvalidFederationTime, FederateInternalError {

        try {
            if(interactionClass == wejscieDoLekarzaHlaHandle){
                StringBuilder builder = new StringBuilder( "Interaction Received:" );
                int id_pacjenta = EncodingHelpers.decodeInt(theInteraction.getValue(0));
                LekarzAmbassador.lista.add(id_pacjenta);
                double godzina = EncodingHelpers.decodeDouble(theInteraction.getValue(1));
                builder.append("Dodano Pacjenta nr " + id_pacjenta + " do lekarza o godzinie " + godzina);
                log( builder.toString() );
            }
            else if(interactionClass == przeniesienieHlaHandle && EncodingHelpers.decodeInt(theInteraction.getValue(1))==5){
                LekarzAmbassador.iloscWolnychGabinetow++;
            }
        } catch (ArrayIndexOutOfBounds e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public void removeObjectInstance(int i, byte[] bytes) throws ObjectNotKnown, FederateInternalError {

    }

    @Override
    public void removeObjectInstance(int i, byte[] bytes, LogicalTime logicalTime, EventRetractionHandle eventRetractionHandle) throws ObjectNotKnown, InvalidFederationTime, FederateInternalError {

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

    /**
     * The RTI has informed us that time regulation is now enabled.
     */
    public void timeRegulationEnabled( LogicalTime theFederateTime )
    {
        this.federateTime = convertTime( theFederateTime );
        this.isRegulating = true;
    }

    public void timeConstrainedEnabled( LogicalTime theFederateTime )
    {
        this.federateTime = convertTime( theFederateTime );
        this.isConstrained = true;
    }

    public void timeAdvanceGrant( LogicalTime theTime )
    {
        this.federateTime = convertTime( theTime );
        this.isAdvancing = false;
    }

    @Override
    public void requestRetraction(EventRetractionHandle eventRetractionHandle) throws EventNotKnown, FederateInternalError {

    }
}

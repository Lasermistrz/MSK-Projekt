package MSK.Gabinet;

import MSK.GUI.MainFederate;
import hla.rti.jlc.EncodingHelpers;
import hla.rti1516e.*;
import hla.rti1516e.exceptions.InvalidLogicalTime;
import hla.rti1516e.time.HLAfloat64Time;
import org.portico.impl.hla1516e.types.time.DoubleTime;

import java.util.ArrayList;
import java.util.Set;

public class GabinetAmbassador implements hla.rti1516e.FederateAmbassador {
    private GabinetFederate federate;
    protected double federateTime        = 0.0;
    protected double federateLookahead   = 1.0;

    protected boolean isRegulating       = false;
    protected boolean isConstrained      = false;
    protected boolean isAdvancing        = false;

    protected boolean isAnnounced        = false;
    protected boolean isReadyToRun       = false;
    protected boolean running 			 = true;
    protected InteractionClassHandle przeniesieniePacjentaHlaHandle;
    public static ArrayList<Integer> lista = new ArrayList<>();

    public GabinetAmbassador(GabinetFederate fed){
        this.federate=fed;
    }

    private double convertTime( LogicalTime logicalTime ) throws InvalidLogicalTime {
        // PORTICO SPECIFIC!!
        return DoubleTime.fromTime(logicalTime);
    }

    private void log( String message )
    {
        System.out.println( "GabinetAmbassador : " + message );
    }

    public void synchronizationPointRegistrationFailed( String label )
    {
        log( "Failed to register sync point: " + label );
    }

    @Override
    public void connectionLost(String s) throws hla.rti1516e.exceptions.FederateInternalError {

    }

    @Override
    public void reportFederationExecutions(FederationExecutionInformationSet federationExecutionInformationSet) throws hla.rti1516e.exceptions.FederateInternalError {

    }

    public void synchronizationPointRegistrationSucceeded(String label )
    {
        log( "Successfully registered sync point: " + label );
    }

    @Override
    public void synchronizationPointRegistrationFailed(String s, SynchronizationPointFailureReason synchronizationPointFailureReason) throws hla.rti1516e.exceptions.FederateInternalError {

    }

    public void announceSynchronizationPoint( String label, byte[] tag )
    {
        log( "Synchronization point announced: " + label );
        if( label.equals(MainFederate.READY_TO_RUN) )
            this.isAnnounced = true;
    }

    @Override
    public void federationSynchronized(String s, hla.rti1516e.FederateHandleSet federateHandleSet) throws hla.rti1516e.exceptions.FederateInternalError {
        log( "Federation Synchronized: " + s );
        if( s.equals(MainFederate.READY_TO_RUN) )
            this.isReadyToRun = true;
    }

    public void federationSynchronized( String label )
    {
        log( "Federation Synchronized: " + label );
        if( label.equals(MainFederate.READY_TO_RUN) )
            this.isReadyToRun = true;
    }

    @Override
    public void initiateFederateSave(String s) {

    }

    @Override
    public void initiateFederateSave(String s, hla.rti1516e.LogicalTime logicalTime) throws hla.rti1516e.exceptions.FederateInternalError {

    }

    @Override
    public void federationSaved() {

    }

    @Override
    public void federationNotSaved(SaveFailureReason saveFailureReason) throws hla.rti1516e.exceptions.FederateInternalError {

    }

    @Override
    public void federationSaveStatusResponse(FederateHandleSaveStatusPair[] federateHandleSaveStatusPairs) throws hla.rti1516e.exceptions.FederateInternalError {

    }

    @Override
    public void requestFederationRestoreSucceeded(String s) {

    }

    @Override
    public void requestFederationRestoreFailed(String s) throws hla.rti1516e.exceptions.FederateInternalError {

    }

    @Override
    public void federationRestoreBegun() {

    }

    @Override
    public void initiateFederateRestore(String s, String s1, FederateHandle federateHandle) throws hla.rti1516e.exceptions.FederateInternalError {

    }


    @Override
    public void federationRestored() {

    }

    @Override
    public void federationNotRestored(RestoreFailureReason restoreFailureReason) throws hla.rti1516e.exceptions.FederateInternalError {

    }

    @Override
    public void federationRestoreStatusResponse(FederateRestoreStatus[] federateRestoreStatuses) throws hla.rti1516e.exceptions.FederateInternalError {

    }

    @Override
    public void startRegistrationForObjectClass(ObjectClassHandle objectClassHandle) throws hla.rti1516e.exceptions.FederateInternalError {

    }

    @Override
    public void stopRegistrationForObjectClass(ObjectClassHandle objectClassHandle) throws hla.rti1516e.exceptions.FederateInternalError {

    }

    @Override
    public void turnInteractionsOn(InteractionClassHandle interactionClassHandle) throws hla.rti1516e.exceptions.FederateInternalError {

    }

    @Override
    public void turnInteractionsOff(InteractionClassHandle interactionClassHandle) throws hla.rti1516e.exceptions.FederateInternalError {

    }

    @Override
    public void objectInstanceNameReservationSucceeded(String s) throws hla.rti1516e.exceptions.FederateInternalError {

    }

    @Override
    public void objectInstanceNameReservationFailed(String s) throws hla.rti1516e.exceptions.FederateInternalError {

    }

    @Override
    public void multipleObjectInstanceNameReservationSucceeded(Set<String> set) throws hla.rti1516e.exceptions.FederateInternalError {

    }

    @Override
    public void multipleObjectInstanceNameReservationFailed(Set<String> set) throws hla.rti1516e.exceptions.FederateInternalError {

    }

    @Override
    public void discoverObjectInstance(ObjectInstanceHandle objectInstanceHandle, ObjectClassHandle objectClassHandle, String s) throws hla.rti1516e.exceptions.FederateInternalError {

    }

    @Override
    public void discoverObjectInstance(ObjectInstanceHandle objectInstanceHandle, ObjectClassHandle objectClassHandle, String s, FederateHandle federateHandle) throws hla.rti1516e.exceptions.FederateInternalError {

    }

    @Override
    public void reflectAttributeValues(ObjectInstanceHandle objectInstanceHandle, AttributeHandleValueMap attributeHandleValueMap, byte[] bytes, OrderType orderType, TransportationTypeHandle transportationTypeHandle, SupplementalReflectInfo supplementalReflectInfo) throws hla.rti1516e.exceptions.FederateInternalError {

    }

    @Override
    public void reflectAttributeValues(ObjectInstanceHandle objectInstanceHandle, AttributeHandleValueMap attributeHandleValueMap, byte[] bytes, OrderType orderType, TransportationTypeHandle transportationTypeHandle, hla.rti1516e.LogicalTime logicalTime, OrderType orderType1, SupplementalReflectInfo supplementalReflectInfo) throws hla.rti1516e.exceptions.FederateInternalError {

    }

    @Override
    public void reflectAttributeValues(ObjectInstanceHandle objectInstanceHandle, AttributeHandleValueMap attributeHandleValueMap, byte[] bytes, OrderType orderType, TransportationTypeHandle transportationTypeHandle, hla.rti1516e.LogicalTime logicalTime, OrderType orderType1, MessageRetractionHandle messageRetractionHandle, SupplementalReflectInfo supplementalReflectInfo) throws hla.rti1516e.exceptions.FederateInternalError {

    }

    @Override
    public void receiveInteraction(InteractionClassHandle interactionClassHandle, ParameterHandleValueMap parameterHandleValueMap,
                                   byte[] bytes, OrderType orderType, TransportationTypeHandle transportationTypeHandle,
                                   SupplementalReceiveInfo supplementalReceiveInfo) throws hla.rti1516e.exceptions.FederateInternalError {
        this.receiveInteraction( interactionClassHandle,
                parameterHandleValueMap,
                bytes,
                orderType,
                transportationTypeHandle,
                null,
                orderType,
                supplementalReceiveInfo );
    }

    @Override
    public void receiveInteraction(InteractionClassHandle interactionClassHandle, ParameterHandleValueMap parameterHandleValueMap, byte[] bytes, OrderType orderType, TransportationTypeHandle transportationTypeHandle, hla.rti1516e.LogicalTime logicalTime, OrderType orderType1, SupplementalReceiveInfo supplementalReceiveInfo) throws hla.rti1516e.exceptions.FederateInternalError {
        if(interactionClassHandle.equals(przeniesieniePacjentaHlaHandle) && EncodingHelpers.decodeInt(parameterHandleValueMap.get(GabinetFederate.miejsceKoncoweHandle))==3){
            StringBuilder builder = new StringBuilder( "Interaction Received:" );
            int id_pacjenta = EncodingHelpers.decodeInt(parameterHandleValueMap.get(GabinetFederate.idPacjentaHandle));
            GabinetAmbassador.lista.add(id_pacjenta);
            builder.append("Dodano Pacjenta nr " + id_pacjenta + " do Gabinetu o godzinie " + logicalTime.toString());
            log( builder.toString() );
        }
    }

    @Override
    public void receiveInteraction(InteractionClassHandle interactionClassHandle, ParameterHandleValueMap parameterHandleValueMap,
                                   byte[] bytes, OrderType orderType, TransportationTypeHandle transportationTypeHandle,
                                   hla.rti1516e.LogicalTime logicalTime, OrderType orderType1,
                                   MessageRetractionHandle messageRetractionHandle, SupplementalReceiveInfo supplementalReceiveInfo) throws hla.rti1516e.exceptions.FederateInternalError {

    }

    @Override
    public void removeObjectInstance(ObjectInstanceHandle objectInstanceHandle, byte[] bytes, OrderType orderType, SupplementalRemoveInfo supplementalRemoveInfo) throws hla.rti1516e.exceptions.FederateInternalError {

    }

    @Override
    public void removeObjectInstance(ObjectInstanceHandle objectInstanceHandle, byte[] bytes, OrderType orderType, hla.rti1516e.LogicalTime logicalTime, OrderType orderType1, SupplementalRemoveInfo supplementalRemoveInfo) throws hla.rti1516e.exceptions.FederateInternalError {

    }

    @Override
    public void removeObjectInstance(ObjectInstanceHandle objectInstanceHandle, byte[] bytes, OrderType orderType, hla.rti1516e.LogicalTime logicalTime, OrderType orderType1, MessageRetractionHandle messageRetractionHandle, SupplementalRemoveInfo supplementalRemoveInfo) throws hla.rti1516e.exceptions.FederateInternalError {

    }

    @Override
    public void attributesInScope(ObjectInstanceHandle objectInstanceHandle, hla.rti1516e.AttributeHandleSet attributeHandleSet) throws hla.rti1516e.exceptions.FederateInternalError {

    }

    @Override
    public void attributesOutOfScope(ObjectInstanceHandle objectInstanceHandle, hla.rti1516e.AttributeHandleSet attributeHandleSet) throws hla.rti1516e.exceptions.FederateInternalError {

    }

    @Override
    public void provideAttributeValueUpdate(ObjectInstanceHandle objectInstanceHandle, hla.rti1516e.AttributeHandleSet attributeHandleSet, byte[] bytes) throws hla.rti1516e.exceptions.FederateInternalError {

    }

    @Override
    public void turnUpdatesOnForObjectInstance(ObjectInstanceHandle objectInstanceHandle, hla.rti1516e.AttributeHandleSet attributeHandleSet) throws hla.rti1516e.exceptions.FederateInternalError {

    }

    @Override
    public void turnUpdatesOnForObjectInstance(ObjectInstanceHandle objectInstanceHandle, hla.rti1516e.AttributeHandleSet attributeHandleSet, String s) throws hla.rti1516e.exceptions.FederateInternalError {

    }

    @Override
    public void turnUpdatesOffForObjectInstance(ObjectInstanceHandle objectInstanceHandle, hla.rti1516e.AttributeHandleSet attributeHandleSet) throws hla.rti1516e.exceptions.FederateInternalError {

    }

    @Override
    public void confirmAttributeTransportationTypeChange(ObjectInstanceHandle objectInstanceHandle, hla.rti1516e.AttributeHandleSet attributeHandleSet, TransportationTypeHandle transportationTypeHandle) throws hla.rti1516e.exceptions.FederateInternalError {

    }

    @Override
    public void reportAttributeTransportationType(ObjectInstanceHandle objectInstanceHandle, AttributeHandle attributeHandle, TransportationTypeHandle transportationTypeHandle) throws hla.rti1516e.exceptions.FederateInternalError {

    }

    @Override
    public void confirmInteractionTransportationTypeChange(InteractionClassHandle interactionClassHandle, TransportationTypeHandle transportationTypeHandle) throws hla.rti1516e.exceptions.FederateInternalError {

    }

    @Override
    public void reportInteractionTransportationType(FederateHandle federateHandle, InteractionClassHandle interactionClassHandle, TransportationTypeHandle transportationTypeHandle) throws hla.rti1516e.exceptions.FederateInternalError {

    }

    @Override
    public void requestAttributeOwnershipAssumption(ObjectInstanceHandle objectInstanceHandle, hla.rti1516e.AttributeHandleSet attributeHandleSet, byte[] bytes) throws hla.rti1516e.exceptions.FederateInternalError {

    }

    @Override
    public void requestDivestitureConfirmation(ObjectInstanceHandle objectInstanceHandle, hla.rti1516e.AttributeHandleSet attributeHandleSet) throws hla.rti1516e.exceptions.FederateInternalError {

    }

    @Override
    public void attributeOwnershipAcquisitionNotification(ObjectInstanceHandle objectInstanceHandle, hla.rti1516e.AttributeHandleSet attributeHandleSet, byte[] bytes) throws hla.rti1516e.exceptions.FederateInternalError {

    }

    @Override
    public void attributeOwnershipUnavailable(ObjectInstanceHandle objectInstanceHandle, hla.rti1516e.AttributeHandleSet attributeHandleSet) throws hla.rti1516e.exceptions.FederateInternalError {

    }

    @Override
    public void requestAttributeOwnershipRelease(ObjectInstanceHandle objectInstanceHandle, hla.rti1516e.AttributeHandleSet attributeHandleSet, byte[] bytes) throws hla.rti1516e.exceptions.FederateInternalError {

    }

    @Override
    public void confirmAttributeOwnershipAcquisitionCancellation(ObjectInstanceHandle objectInstanceHandle, hla.rti1516e.AttributeHandleSet attributeHandleSet) throws hla.rti1516e.exceptions.FederateInternalError {

    }

    @Override
    public void informAttributeOwnership(ObjectInstanceHandle objectInstanceHandle, AttributeHandle attributeHandle, FederateHandle federateHandle) throws hla.rti1516e.exceptions.FederateInternalError {

    }

    @Override
    public void attributeIsNotOwned(ObjectInstanceHandle objectInstanceHandle, AttributeHandle attributeHandle) throws hla.rti1516e.exceptions.FederateInternalError {

    }

    @Override
    public void attributeIsOwnedByRTI(ObjectInstanceHandle objectInstanceHandle, AttributeHandle attributeHandle) throws hla.rti1516e.exceptions.FederateInternalError {

    }

    @Override
    public void timeRegulationEnabled(hla.rti1516e.LogicalTime logicalTime) throws hla.rti1516e.exceptions.FederateInternalError {
        this.federateTime = ((HLAfloat64Time)logicalTime).getValue();
        this.isRegulating = true;
    }

    @Override
    public void timeConstrainedEnabled(hla.rti1516e.LogicalTime logicalTime) throws hla.rti1516e.exceptions.FederateInternalError {
        this.federateTime = ((HLAfloat64Time)logicalTime).getValue();
        this.isConstrained = true;
    }

    @Override
    public void timeAdvanceGrant(hla.rti1516e.LogicalTime logicalTime) throws hla.rti1516e.exceptions.FederateInternalError {
        this.federateTime = ((HLAfloat64Time)logicalTime).getValue();
        this.isAdvancing = false;
    }

    @Override
    public void requestRetraction(MessageRetractionHandle messageRetractionHandle) throws hla.rti1516e.exceptions.FederateInternalError {

    }
   /* @Override
    public void receiveInteraction(int interactionClass, ReceivedInteraction theInteraction, byte[] tag, LogicalTime theTime, EventRetractionHandle eventRetractionHandle) throws InteractionClassNotKnown, InteractionParameterNotKnown, InvalidFederationTime, FederateInternalError {

        try {
            if(interactionClass == przeniesieniePacjentaHlaHandle && EncodingHelpers.decodeInt(theInteraction.getValue(1))==3){
                StringBuilder builder = new StringBuilder( "Interaction Received:" );
                int id_pacjenta = EncodingHelpers.decodeInt(theInteraction.getValue(0));
                GabinetAmbassador.lista.add(id_pacjenta);
                builder.append("Dodano Pacjenta nr " + id_pacjenta + " do Gabinetu o godzinie " + theTime);
                log( builder.toString() );
            }
        } catch (ArrayIndexOutOfBounds e) {
            throw new RuntimeException(e);
        }
    }*/

}

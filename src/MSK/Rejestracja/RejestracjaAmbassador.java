package MSK.Rejestracja;

import MSK.GUI.MainFederate;
import MSK.Gabinet.GabinetFederate;
import MSK.Pacjent.PacjentFederate;
import hla.rti.AttributeHandleSet;
import hla.rti.jlc.EncodingHelpers;
import hla.rti1516.SpecifiedSaveLabelDoesNotExist;
import hla.rti1516e.*;
import hla.rti1516e.exceptions.FederateInternalError;
import hla.rti1516e.exceptions.InvalidLogicalTime;
import hla.rti1516e.exceptions.UnableToPerformSave;
import hla.rti1516e.time.HLAfloat64Time;
import org.portico.impl.hla1516e.types.time.DoubleTime;

import java.util.ArrayList;
import java.util.Set;

public class RejestracjaAmbassador implements hla.rti1516e.FederateAmbassador {
    //----------------------------------------------------------
    //                    STATIC VARIABLES
    //----------------------------------------------------------

    //----------------------------------------------------------
    //                   INSTANCE VARIABLES
    //----------------------------------------------------------
    // these variables are accessible in the package
    private RejestracjaFederate federate;
    protected double federateTime = 0.0;
    protected double federateLookahead = 1.0;

    protected boolean isRegulating = false;
    protected boolean isConstrained = false;
    protected boolean isAdvancing = false;

    protected boolean isAnnounced = false;
    protected boolean isReadyToRun = false;

    protected boolean running = true;
    protected InteractionClassHandle wejscieDoPrzychodniHandle;
    protected InteractionClassHandle przeniesieniePacjentaHandle;
    protected InteractionClassHandle wejscieDoLekarzaHandle;
    public static ArrayList<Integer> lista = new ArrayList<>();


    public RejestracjaAmbassador(RejestracjaFederate fed){
        this.federate=fed;
    }

    private void log(String message) {
        System.out.println("RejestracjaAmbassador    : " + message);
    }


    @Override
    public void connectionLost(String s) throws FederateInternalError {

    }

    @Override
    public void reportFederationExecutions(FederationExecutionInformationSet federationExecutionInformationSet) throws FederateInternalError {

    }

    public void synchronizationPointRegistrationSucceeded(String label) {
        log("Successfully registered sync point: " + label);
    }

    @Override
    public void synchronizationPointRegistrationFailed(String s, SynchronizationPointFailureReason synchronizationPointFailureReason) throws FederateInternalError {

    }

    public void announceSynchronizationPoint(String label, byte[] tag) {
        log("Synchronization point announced: " + label);
        if (label.equals(MainFederate.READY_TO_RUN))
            this.isAnnounced = true;
    }

    @Override
    public void federationSynchronized(String s, hla.rti1516e.FederateHandleSet federateHandleSet) throws hla.rti1516e.exceptions.FederateInternalError {
        log( "Federation Synchronized: " + s );
        if( s.equals(MainFederate.READY_TO_RUN) )
            this.isReadyToRun = true;
    }


    @Override
    public void initiateFederateSave(String s) throws FederateInternalError {

    }

    @Override
    public void initiateFederateSave(String s, hla.rti1516e.LogicalTime logicalTime) throws hla.rti1516e.exceptions.FederateInternalError {

    }

    @Override
    public void federationSaved() throws FederateInternalError {

    }

    @Override
    public void federationNotSaved(SaveFailureReason saveFailureReason) throws hla.rti1516e.exceptions.FederateInternalError {

    }

    @Override
    public void federationSaveStatusResponse(FederateHandleSaveStatusPair[] federateHandleSaveStatusPairs) throws hla.rti1516e.exceptions.FederateInternalError {

    }

    @Override
    public void requestFederationRestoreSucceeded(String s) throws FederateInternalError {

    }

    @Override
    public void requestFederationRestoreFailed(String s) throws hla.rti1516e.exceptions.FederateInternalError {

    }

    @Override
    public void federationRestoreBegun() throws FederateInternalError {

    }

    @Override
    public void initiateFederateRestore(String s, String s1, FederateHandle federateHandle) throws hla.rti1516e.exceptions.FederateInternalError {

    }

    @Override
    public void federationRestored() throws FederateInternalError {

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
    public void receiveInteraction(InteractionClassHandle interactionClassHandle, ParameterHandleValueMap parameterHandleValueMap, byte[] bytes, OrderType orderType, TransportationTypeHandle transportationTypeHandle, SupplementalReceiveInfo supplementalReceiveInfo) throws hla.rti1516e.exceptions.FederateInternalError {
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
        if (interactionClassHandle.equals(wejscieDoPrzychodniHandle)) {
                StringBuilder builder = new StringBuilder("Interaction Received:");
                int id_pacjenta = EncodingHelpers.decodeInt(parameterHandleValueMap.get(RejestracjaFederate.idPacjentaPrzychodniaHandle));
                double godzina_wejscia = EncodingHelpers.decodeDouble(parameterHandleValueMap.get(RejestracjaFederate.godzinaWejsciaHandle));
                builder.append("Przybyl pacjent nr " + id_pacjenta + " , time=" + godzina_wejscia);
                lista.add(id_pacjenta);
                log(builder.toString());
        } else if (interactionClassHandle.equals(wejscieDoLekarzaHandle)) {
            RejestracjaFederate.iloscWolnychMiejscWPoczekalni++;
        }
    }

    @Override
    public void receiveInteraction(InteractionClassHandle interactionClassHandle, ParameterHandleValueMap parameterHandleValueMap, byte[] bytes, OrderType orderType, TransportationTypeHandle transportationTypeHandle, hla.rti1516e.LogicalTime logicalTime, OrderType orderType1, MessageRetractionHandle messageRetractionHandle, SupplementalReceiveInfo supplementalReceiveInfo) throws hla.rti1516e.exceptions.FederateInternalError {

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
    public void attributeIsOwnedByRTI(ObjectInstanceHandle objectInstanceHandle, AttributeHandle attributeHandle) throws FederateInternalError {

    }

    @Override
    public void timeRegulationEnabled(hla.rti1516e.LogicalTime logicalTime) throws FederateInternalError {
        this.federateTime = ((HLAfloat64Time)logicalTime).getValue();
        this.isRegulating = true;
    }

    @Override
    public void timeConstrainedEnabled(hla.rti1516e.LogicalTime logicalTime) throws FederateInternalError {
        this.federateTime = ((HLAfloat64Time)logicalTime).getValue();
        this.isConstrained = true;
    }

    @Override
    public void timeAdvanceGrant(hla.rti1516e.LogicalTime logicalTime) throws FederateInternalError {
        this.federateTime = ((HLAfloat64Time)logicalTime).getValue();
        this.isAdvancing = false;
    }

    @Override
    public void requestRetraction(MessageRetractionHandle messageRetractionHandle) throws FederateInternalError {

    }
}

package MSK.Statystyka;

import MSK.GUI.MainFederate;
import hla.rti1516e.*;
import hla.rti.jlc.EncodingHelpers;
import hla.rti1516e.exceptions.FederateInternalError;
import hla.rti1516e.exceptions.InvalidLogicalTime;
import hla.rti1516e.time.HLAfloat64Time;
import org.portico.impl.hla1516e.types.time.DoubleTime;

import java.util.Set;

public class StatystykaAmbassador implements FederateAmbassador{
    private StatystykaFederate federate;
    protected double federateTime        = 0.0;
    protected double federateLookahead   = 1.0;

    protected boolean isRegulating       = false;
    protected boolean isConstrained      = false;
    protected boolean isAdvancing        = false;

    protected boolean isAnnounced        = false;
    protected boolean isReadyToRun       = false;
    protected boolean running 			 = true;
    protected InteractionClassHandle wejscieDoPrzychodniHandle;
    protected InteractionClassHandle przeniesieniePacjentaHandle;
    protected InteractionClassHandle wejscieDoLekarzaHandle;
    public static Statistics listaPacjentow = new Statistics();

    public StatystykaAmbassador(StatystykaFederate federate) {
        this.federate = federate;
    }

    private double convertTime( LogicalTime logicalTime ) throws InvalidLogicalTime {
        // PORTICO SPECIFIC!!
        return DoubleTime.fromTime(logicalTime);
    }

    private void log( String message )
    {
        System.out.println( "StatystykaAmbassador : " + message );
    }

    @Override
    public void connectionLost(String s) throws FederateInternalError {

    }

    @Override
    public void reportFederationExecutions(FederationExecutionInformationSet federationExecutionInformationSet) throws FederateInternalError {

    }

    public void synchronizationPointRegistrationSucceeded(String label )
    {
        log( "Successfully registered sync point: " + label );
    }

    @Override
    public void synchronizationPointRegistrationFailed(String s, SynchronizationPointFailureReason synchronizationPointFailureReason) throws FederateInternalError {

    }

    public void announceSynchronizationPoint( String label, byte[] tag )
    {
        log( "Synchronization point announced: " + label );
        if( label.equals(MainFederate.READY_TO_RUN) )
            this.isAnnounced = true;
    }

    @Override
    public void federationSynchronized(String s, FederateHandleSet federateHandleSet) throws FederateInternalError {
        log( "Federation Synchronized: " + s );
        if( s.equals(MainFederate.READY_TO_RUN) )
            this.isReadyToRun = true;
    }

    @Override
    public void initiateFederateSave(String s) throws FederateInternalError {

    }

    @Override
    public void initiateFederateSave(String s, LogicalTime logicalTime) throws FederateInternalError {

    }

    @Override
    public void federationSaved() throws FederateInternalError {

    }

    @Override
    public void federationNotSaved(SaveFailureReason saveFailureReason) throws FederateInternalError {

    }

    @Override
    public void federationSaveStatusResponse(FederateHandleSaveStatusPair[] federateHandleSaveStatusPairs) throws FederateInternalError {

    }

    @Override
    public void requestFederationRestoreSucceeded(String s) throws FederateInternalError {

    }

    @Override
    public void requestFederationRestoreFailed(String s) throws FederateInternalError {

    }

    @Override
    public void federationRestoreBegun() throws FederateInternalError {

    }

    @Override
    public void initiateFederateRestore(String s, String s1, FederateHandle federateHandle) throws FederateInternalError {

    }

    @Override
    public void federationRestored() throws FederateInternalError {

    }

    @Override
    public void federationNotRestored(RestoreFailureReason restoreFailureReason) throws FederateInternalError {

    }

    @Override
    public void federationRestoreStatusResponse(FederateRestoreStatus[] federateRestoreStatuses) throws FederateInternalError {

    }

    @Override
    public void startRegistrationForObjectClass(ObjectClassHandle objectClassHandle) throws FederateInternalError {

    }

    @Override
    public void stopRegistrationForObjectClass(ObjectClassHandle objectClassHandle) throws FederateInternalError {

    }

    @Override
    public void turnInteractionsOn(InteractionClassHandle interactionClassHandle) throws FederateInternalError {

    }

    @Override
    public void turnInteractionsOff(InteractionClassHandle interactionClassHandle) throws FederateInternalError {

    }

    @Override
    public void objectInstanceNameReservationSucceeded(String s) throws FederateInternalError {

    }

    @Override
    public void objectInstanceNameReservationFailed(String s) throws FederateInternalError {

    }

    @Override
    public void multipleObjectInstanceNameReservationSucceeded(Set<String> set) throws FederateInternalError {

    }

    @Override
    public void multipleObjectInstanceNameReservationFailed(Set<String> set) throws FederateInternalError {

    }

    @Override
    public void discoverObjectInstance(ObjectInstanceHandle objectInstanceHandle, ObjectClassHandle objectClassHandle, String s) throws FederateInternalError {

    }

    @Override
    public void discoverObjectInstance(ObjectInstanceHandle objectInstanceHandle, ObjectClassHandle objectClassHandle, String s, FederateHandle federateHandle) throws FederateInternalError {

    }

    @Override
    public void reflectAttributeValues(ObjectInstanceHandle objectInstanceHandle, AttributeHandleValueMap attributeHandleValueMap, byte[] bytes, OrderType orderType, TransportationTypeHandle transportationTypeHandle, SupplementalReflectInfo supplementalReflectInfo) throws FederateInternalError {

    }

    @Override
    public void reflectAttributeValues(ObjectInstanceHandle objectInstanceHandle, AttributeHandleValueMap attributeHandleValueMap, byte[] bytes, OrderType orderType, TransportationTypeHandle transportationTypeHandle, LogicalTime logicalTime, OrderType orderType1, SupplementalReflectInfo supplementalReflectInfo) throws FederateInternalError {

    }

    @Override
    public void reflectAttributeValues(ObjectInstanceHandle objectInstanceHandle, AttributeHandleValueMap attributeHandleValueMap, byte[] bytes, OrderType orderType, TransportationTypeHandle transportationTypeHandle, LogicalTime logicalTime, OrderType orderType1, MessageRetractionHandle messageRetractionHandle, SupplementalReflectInfo supplementalReflectInfo) throws FederateInternalError {

    }

    @Override
    public void receiveInteraction(InteractionClassHandle interactionClassHandle, ParameterHandleValueMap parameterHandleValueMap, byte[] bytes, OrderType orderType, TransportationTypeHandle transportationTypeHandle, SupplementalReceiveInfo supplementalReceiveInfo) throws FederateInternalError {
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
    public void receiveInteraction(InteractionClassHandle interactionClassHandle, ParameterHandleValueMap parameterHandleValueMap, byte[] bytes, OrderType orderType, TransportationTypeHandle transportationTypeHandle, LogicalTime logicalTime, OrderType orderType1, SupplementalReceiveInfo supplementalReceiveInfo) throws FederateInternalError {
        StringBuilder builder = new StringBuilder("Interaction Received:");
        try {
            if (interactionClassHandle.equals(wejscieDoPrzychodniHandle) ) {
                int id_pacjenta = EncodingHelpers.decodeInt(parameterHandleValueMap.get(StatystykaFederate.idPacjentaPrzychodniaHandle));
                double godzina_wejscia = EncodingHelpers.decodeDouble(parameterHandleValueMap.get(StatystykaFederate.godzinaWejsciaHandle));
                builder.append("Przybyl pacjent nr " + id_pacjenta + " do Przychodni, time=" + godzina_wejscia);
                StatystykaAmbassador.listaPacjentow.EnterToRegistration(id_pacjenta,godzina_wejscia);
            } else if (interactionClassHandle.equals(wejscieDoLekarzaHandle)) {
                int id_pacjenta = EncodingHelpers.decodeInt(parameterHandleValueMap.get(StatystykaFederate.idPacjentaLekarzHandle));
                double godzina_wejscia = EncodingHelpers.decodeDouble(parameterHandleValueMap.get(StatystykaFederate.godzinaWejsciaDoLekarzaHandle));
                builder.append("Przybyl pacjent nr " + id_pacjenta + " do Lekarza, time=" + godzina_wejscia);
                StatystykaAmbassador.listaPacjentow.EnterToDoctor(id_pacjenta,godzina_wejscia);
            } else if (interactionClassHandle.equals(przeniesieniePacjentaHandle) && EncodingHelpers.decodeInt(parameterHandleValueMap.get(StatystykaFederate.miejsceKoncoweHandle))==1) {
                int id_pacjenta = EncodingHelpers.decodeInt(parameterHandleValueMap.get(StatystykaFederate.idPacjentaPrzeniesienieHandle));
                builder.append("Przybyl pacjent nr " + id_pacjenta + " do Poczekalni");
                StatystykaAmbassador.listaPacjentow.EnterToWaitingRoom(id_pacjenta,convertTime(logicalTime));
            } else if (interactionClassHandle.equals(przeniesieniePacjentaHandle) && EncodingHelpers.decodeInt(parameterHandleValueMap.get(StatystykaFederate.miejsceKoncoweHandle)) == 3) {
                int id_pacjenta = EncodingHelpers.decodeInt(parameterHandleValueMap.get(StatystykaFederate.idPacjentaPrzeniesienieHandle));
                builder.append("Pacjent nr " + id_pacjenta + " przeniesiony do Gabinetu ");
                StatystykaAmbassador.listaPacjentow.EnterToConsultingRoom(id_pacjenta,convertTime(logicalTime));
            } else if (interactionClassHandle.equals(przeniesieniePacjentaHandle) && EncodingHelpers.decodeInt(parameterHandleValueMap.get(StatystykaFederate.miejsceKoncoweHandle)) == 4) {
                int id_pacjenta = EncodingHelpers.decodeInt(parameterHandleValueMap.get(StatystykaFederate.idPacjentaPrzeniesienieHandle));
                builder.append("Przybyl pacjent nr " + id_pacjenta + " obsluzony przez lekarza");
                StatystykaAmbassador.listaPacjentow.LeavesClinic(id_pacjenta,convertTime(logicalTime));
            } else if (interactionClassHandle.equals(przeniesieniePacjentaHandle) && EncodingHelpers.decodeInt(parameterHandleValueMap.get(StatystykaFederate.miejsceKoncoweHandle)) == 5) {
                int id_pacjenta = EncodingHelpers.decodeInt(parameterHandleValueMap.get(StatystykaFederate.idPacjentaPrzeniesienieHandle));
                builder.append("Przybyl pacjent nr " + id_pacjenta + " obsluzony w Gabinecie");
                StatystykaAmbassador.listaPacjentow.LeavesClinic(id_pacjenta,convertTime(logicalTime));
            }
        }catch (Exception e){}


        log(builder.toString());
    }

    @Override
    public void receiveInteraction(InteractionClassHandle interactionClassHandle, ParameterHandleValueMap parameterHandleValueMap, byte[] bytes, OrderType orderType, TransportationTypeHandle transportationTypeHandle, LogicalTime logicalTime, OrderType orderType1, MessageRetractionHandle messageRetractionHandle, SupplementalReceiveInfo supplementalReceiveInfo) throws FederateInternalError {

    }

    @Override
    public void removeObjectInstance(ObjectInstanceHandle objectInstanceHandle, byte[] bytes, OrderType orderType, SupplementalRemoveInfo supplementalRemoveInfo) throws FederateInternalError {

    }

    @Override
    public void removeObjectInstance(ObjectInstanceHandle objectInstanceHandle, byte[] bytes, OrderType orderType, LogicalTime logicalTime, OrderType orderType1, SupplementalRemoveInfo supplementalRemoveInfo) throws FederateInternalError {

    }

    @Override
    public void removeObjectInstance(ObjectInstanceHandle objectInstanceHandle, byte[] bytes, OrderType orderType, LogicalTime logicalTime, OrderType orderType1, MessageRetractionHandle messageRetractionHandle, SupplementalRemoveInfo supplementalRemoveInfo) throws FederateInternalError {

    }

    @Override
    public void attributesInScope(ObjectInstanceHandle objectInstanceHandle, AttributeHandleSet attributeHandleSet) throws FederateInternalError {

    }

    @Override
    public void attributesOutOfScope(ObjectInstanceHandle objectInstanceHandle, AttributeHandleSet attributeHandleSet) throws FederateInternalError {

    }

    @Override
    public void provideAttributeValueUpdate(ObjectInstanceHandle objectInstanceHandle, AttributeHandleSet attributeHandleSet, byte[] bytes) throws FederateInternalError {

    }

    @Override
    public void turnUpdatesOnForObjectInstance(ObjectInstanceHandle objectInstanceHandle, AttributeHandleSet attributeHandleSet) throws FederateInternalError {

    }

    @Override
    public void turnUpdatesOnForObjectInstance(ObjectInstanceHandle objectInstanceHandle, AttributeHandleSet attributeHandleSet, String s) throws FederateInternalError {

    }

    @Override
    public void turnUpdatesOffForObjectInstance(ObjectInstanceHandle objectInstanceHandle, AttributeHandleSet attributeHandleSet) throws FederateInternalError {

    }

    @Override
    public void confirmAttributeTransportationTypeChange(ObjectInstanceHandle objectInstanceHandle, AttributeHandleSet attributeHandleSet, TransportationTypeHandle transportationTypeHandle) throws FederateInternalError {

    }

    @Override
    public void reportAttributeTransportationType(ObjectInstanceHandle objectInstanceHandle, AttributeHandle attributeHandle, TransportationTypeHandle transportationTypeHandle) throws FederateInternalError {

    }

    @Override
    public void confirmInteractionTransportationTypeChange(InteractionClassHandle interactionClassHandle, TransportationTypeHandle transportationTypeHandle) throws FederateInternalError {

    }

    @Override
    public void reportInteractionTransportationType(FederateHandle federateHandle, InteractionClassHandle interactionClassHandle, TransportationTypeHandle transportationTypeHandle) throws FederateInternalError {

    }

    @Override
    public void requestAttributeOwnershipAssumption(ObjectInstanceHandle objectInstanceHandle, AttributeHandleSet attributeHandleSet, byte[] bytes) throws FederateInternalError {

    }

    @Override
    public void requestDivestitureConfirmation(ObjectInstanceHandle objectInstanceHandle, AttributeHandleSet attributeHandleSet) throws FederateInternalError {

    }

    @Override
    public void attributeOwnershipAcquisitionNotification(ObjectInstanceHandle objectInstanceHandle, AttributeHandleSet attributeHandleSet, byte[] bytes) throws FederateInternalError {

    }

    @Override
    public void attributeOwnershipUnavailable(ObjectInstanceHandle objectInstanceHandle, AttributeHandleSet attributeHandleSet) throws FederateInternalError {

    }

    @Override
    public void requestAttributeOwnershipRelease(ObjectInstanceHandle objectInstanceHandle, AttributeHandleSet attributeHandleSet, byte[] bytes) throws FederateInternalError {

    }

    @Override
    public void confirmAttributeOwnershipAcquisitionCancellation(ObjectInstanceHandle objectInstanceHandle, AttributeHandleSet attributeHandleSet) throws FederateInternalError {

    }

    @Override
    public void informAttributeOwnership(ObjectInstanceHandle objectInstanceHandle, AttributeHandle attributeHandle, FederateHandle federateHandle) throws FederateInternalError {

    }

    @Override
    public void attributeIsNotOwned(ObjectInstanceHandle objectInstanceHandle, AttributeHandle attributeHandle) throws FederateInternalError {

    }

    @Override
    public void attributeIsOwnedByRTI(ObjectInstanceHandle objectInstanceHandle, AttributeHandle attributeHandle) throws FederateInternalError {

    }

    @Override
    public void timeRegulationEnabled(LogicalTime logicalTime) throws FederateInternalError {
        this.federateTime = ((HLAfloat64Time)logicalTime).getValue();
        this.isRegulating = true;
    }

    @Override
    public void timeConstrainedEnabled(LogicalTime logicalTime) throws FederateInternalError {
        this.federateTime = ((HLAfloat64Time)logicalTime).getValue();
        this.isConstrained = true;
    }

    @Override
    public void timeAdvanceGrant(LogicalTime logicalTime) throws FederateInternalError {
        this.federateTime = ((HLAfloat64Time)logicalTime).getValue();
        this.isAdvancing = false;
    }

    @Override
    public void requestRetraction(MessageRetractionHandle messageRetractionHandle) throws FederateInternalError {

    }
}

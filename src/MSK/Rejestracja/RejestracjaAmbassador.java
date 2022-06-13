package MSK.Rejestracja;

import MSK.GUI.MainFederate;
import hla.rti.*;
import hla.rti.jlc.EncodingHelpers;
import org.portico.impl.hla13.types.DoubleTime;

import java.util.ArrayList;

public class RejestracjaAmbassador implements FederateAmbassador{
    //----------------------------------------------------------
    //                    STATIC VARIABLES
    //----------------------------------------------------------

    //----------------------------------------------------------
    //                   INSTANCE VARIABLES
    //----------------------------------------------------------
    // these variables are accessible in the package
    protected double federateTime        = 0.0;
    protected double federateLookahead   = 1.0;

    protected boolean isRegulating       = false;
    protected boolean isConstrained      = false;
    protected boolean isAdvancing        = false;

    protected boolean isAnnounced        = false;
    protected boolean isReadyToRun       = false;

    protected boolean running 			 = true;
    protected int wejscieDoPrzychodniHandle;
    protected int przeniesienieHlaHandle;
    public static ArrayList<Integer> lista = new ArrayList<>();


    private double convertTime( LogicalTime logicalTime )
    {
        // PORTICO SPECIFIC!!
        return ((DoubleTime)logicalTime).getTime();
    }

    private void log( String message )
    {
        System.out.println( "FederateAmbassador: " + message );
    }

    //////////////////////////////////////////////////////////////////////////
    ////////////////////////// RTI Callback Methods //////////////////////////
    //////////////////////////////////////////////////////////////////////////
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

    public void discoverObjectInstance( int theObject,
                                        int theObjectClass,
                                        String objectName )
    {
        log( "Discoverd Object: handle=" + theObject + ", classHandle=" +
                theObjectClass + ", name=" + objectName );
    }

    public void reflectAttributeValues( int theObject,
                                        ReflectedAttributes theAttributes,
                                        byte[] tag )
    {
        // just pass it on to the other method for printing purposes
        // passing null as the time will let the other method know it
        // it from us, not from the RTI
        reflectAttributeValues( theObject, theAttributes, tag, null, null );
    }

    public void reflectAttributeValues( int theObject,
                                        ReflectedAttributes theAttributes,
                                        byte[] tag,
                                        LogicalTime theTime,
                                        EventRetractionHandle retractionHandle )
    {
        StringBuilder builder = new StringBuilder( "Reflection for object:" );

        // print the handle
        builder.append( " handle=" + theObject );
        // print the tag
        builder.append( ", tag=" + EncodingHelpers.decodeString(tag) );
        // print the time (if we have it) we'll get null if we are just receiving
        // a forwarded call from the other reflect callback above
        if( theTime != null )
        {
            builder.append( ", time=" + convertTime(theTime) );
        }

        // print the attribute information
        builder.append( ", attributeCount=" + theAttributes.size() );
        builder.append( "\n" );
        for( int i = 0; i < theAttributes.size(); i++ )
        {
            try
            {
                // print the attibute handle
                builder.append( "\tattributeHandle=" );
                builder.append( theAttributes.getAttributeHandle(i) );
                // print the attribute value
                builder.append( ", attributeValue=" );
                builder.append(
                        EncodingHelpers.decodeString(theAttributes.getValue(i)) );
                builder.append( "\n" );
            }
            catch( ArrayIndexOutOfBounds aioob )
            {
                // won't happen
            }
        }

        log( builder.toString() );
    }

    public void receiveInteraction( int interactionClass,
                                    ReceivedInteraction theInteraction,
                                    byte[] tag )
    {
        // just pass it on to the other method for printing purposes
        // passing null as the time will let the other method know it
        // it from us, not from the RTI
        receiveInteraction( interactionClass, theInteraction, tag, null, null );
    }

    public void receiveInteraction( int interactionClass,
                                    ReceivedInteraction theInteraction,
                                    byte[] tag,
                                    LogicalTime theTime,
                                    EventRetractionHandle eventRetractionHandle )
    {

        StringBuilder builder = new StringBuilder( "Interaction Received:" );

        if(interactionClass == wejscieDoPrzychodniHandle){
            try {
                int id_pacjenta = EncodingHelpers.decodeInt(theInteraction.getValue(0));
                double godzina_wejscia = EncodingHelpers.decodeDouble(theInteraction.getValue(1));
                builder.append("Przybył pacjent nr " + id_pacjenta + " , time=" + godzina_wejscia);
                lista.add(id_pacjenta);
            } catch (ArrayIndexOutOfBounds e) {
                throw new RuntimeException(e);
            }
        }
        try {
            if(interactionClass == przeniesienieHlaHandle && EncodingHelpers.decodeInt(theInteraction.getValue(1))==2){
                RejestracjaFederate.iloscWRejestracji++;
            }
        } catch (ArrayIndexOutOfBounds e) {
            throw new RuntimeException(e);
        }

        log( builder.toString() );
    }

    public void removeObjectInstance( int theObject, byte[] userSuppliedTag )
    {
        log( "Object Removed: handle=" + theObject );
    }

    public void removeObjectInstance( int theObject,
                                      byte[] userSuppliedTag,
                                      LogicalTime theTime,
                                      EventRetractionHandle retractionHandle )
    {
        log( "Object Removed: handle=" + theObject );
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

package de.symeda.sormas.backend.caze.porthealthinfo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.auditlog.api.Audited;
import de.symeda.sormas.api.caze.porthealthinfo.ConveyanceType;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.common.AbstractDomainObject;

@Entity
@Audited
public class PortHealthInfo extends AbstractDomainObject {

	private static final long serialVersionUID = 3289289799891965437L;
	
	public static final String TABLE_NAME = "casepointofentry";
	
	// Airport
	private String airlineName;
	private String flightNumber;
	private Date departureDateTime;
	private Date arrivalDateTime;
	private YesNoUnknown freeSeating;
	private String seatNumber;
	private String departureAirport;
	private Integer numberOfTransitStops;
	private String transitStopDetails1;
	private String transitStopDetails2;
	private String transitStopDetails3;
	private String transitStopDetails4;
	private String transitStopDetails5;
	
	// Seaport
	private String vesselName;
	private String vesselDetails;
	private String portOfDeparture;
	private String lastPortOfCall;
	
	// Ground Crossing
	private ConveyanceType conveyanceType;
	private String departureLocation;
	private String finalDestination;
	
	// Other
	private String details;

	@Column(length = 512)
	public String getAirlineName() {
		return airlineName;
	}

	public void setAirlineName(String airlineName) {
		this.airlineName = airlineName;
	}

	@Column(length = 512)
	public String getFlightNumber() {
		return flightNumber;
	}

	public void setFlightNumber(String flightNumber) {
		this.flightNumber = flightNumber;
	}

	@Temporal(TemporalType.DATE)
	public Date getDepartureDateTime() {
		return departureDateTime;
	}

	public void setDepartureDateTime(Date departureDateTime) {
		this.departureDateTime = departureDateTime;
	}

	@Temporal(TemporalType.DATE)
	public Date getArrivalDateTime() {
		return arrivalDateTime;
	}

	public void setArrivalDateTime(Date arrivalDateTime) {
		this.arrivalDateTime = arrivalDateTime;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getFreeSeating() {
		return freeSeating;
	}

	public void setFreeSeating(YesNoUnknown freeSeating) {
		this.freeSeating = freeSeating;
	}

	@Column(length = 512)
	public String getSeatNumber() {
		return seatNumber;
	}

	public void setSeatNumber(String seatNumber) {
		this.seatNumber = seatNumber;
	}

	@Column(length = 512)
	public String getDepartureAirport() {
		return departureAirport;
	}

	public void setDepartureAirport(String departureAirport) {
		this.departureAirport = departureAirport;
	}

	public Integer getNumberOfTransitStops() {
		return numberOfTransitStops;
	}

	public void setNumberOfTransitStops(Integer numberOfTransitStops) {
		this.numberOfTransitStops = numberOfTransitStops;
	}

	@Column(length = 512)
	public String getTransitStopDetails1() {
		return transitStopDetails1;
	}

	public void setTransitStopDetails1(String transitStopDetails1) {
		this.transitStopDetails1 = transitStopDetails1;
	}

	@Column(length = 512)
	public String getTransitStopDetails2() {
		return transitStopDetails2;
	}

	public void setTransitStopDetails2(String transitStopDetails2) {
		this.transitStopDetails2 = transitStopDetails2;
	}

	@Column(length = 512)
	public String getTransitStopDetails3() {
		return transitStopDetails3;
	}

	public void setTransitStopDetails3(String transitStopDetails3) {
		this.transitStopDetails3 = transitStopDetails3;
	}

	@Column(length = 512)
	public String getTransitStopDetails4() {
		return transitStopDetails4;
	}

	public void setTransitStopDetails4(String transitStopDetails4) {
		this.transitStopDetails4 = transitStopDetails4;
	}

	@Column(length = 512)
	public String getTransitStopDetails5() {
		return transitStopDetails5;
	}

	public void setTransitStopDetails5(String transitStopDetails5) {
		this.transitStopDetails5 = transitStopDetails5;
	}

	@Column(length = 512)
	public String getVesselName() {
		return vesselName;
	}

	public void setVesselName(String vesselName) {
		this.vesselName = vesselName;
	}

	@Column(length = 512)
	public String getVesselDetails() {
		return vesselDetails;
	}

	public void setVesselDetails(String vesselDetails) {
		this.vesselDetails = vesselDetails;
	}

	@Column(length = 512)
	public String getPortOfDeparture() {
		return portOfDeparture;
	}

	public void setPortOfDeparture(String portOfDeparture) {
		this.portOfDeparture = portOfDeparture;
	}

	@Column(length = 512)
	public String getLastPortOfCall() {
		return lastPortOfCall;
	}

	public void setLastPortOfCall(String lastPortOfCall) {
		this.lastPortOfCall = lastPortOfCall;
	}

	@Enumerated(EnumType.STRING)
	public ConveyanceType getConveyanceType() {
		return conveyanceType;
	}

	public void setConveyanceType(ConveyanceType conveyanceType) {
		this.conveyanceType = conveyanceType;
	}

	@Column(length = 512)
	public String getDepartureLocation() {
		return departureLocation;
	}

	public void setDepartureLocation(String departureLocation) {
		this.departureLocation = departureLocation;
	}

	@Column(length = 512)
	public String getFinalDestination() {
		return finalDestination;
	}

	public void setFinalDestination(String finalDestination) {
		this.finalDestination = finalDestination;
	}

	@Column(length = 512)
	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}
		
}
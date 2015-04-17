package net.creuroja.android.vehicletracking.services;

import android.content.Intent;
import android.test.ServiceTestCase;

import net.creuroja.android.vehicletracking.model.vehicles.Vehicle;

public class PositionUpdaterServiceTest extends ServiceTestCase<PositionUpdaterService> {
	PositionUpdaterService service;
	Vehicle vehicle = new Vehicle(1, "indicative");
	public PositionUpdaterServiceTest() {
		super(PositionUpdaterService.class);
	}

	public void setUp() throws Exception {
		super.setUp();
		Intent intent = configureVehicle();
		startService(intent);
		service = getService();
	}

	private Intent configureVehicle() {
		Intent intent = new Intent(getContext(), PositionUpdaterService.class);
		intent.putExtra(PositionUpdaterService.EXTRA_INDICATIVE, vehicle.indicative);
		intent.putExtra(PositionUpdaterService.EXTRA_VEHICLE_ID, vehicle.id);
		return intent;
	}

	public void testExpectationsUponStart() throws Exception {
		assertTrue(service.apiClient.isConnected() || service.apiClient.isConnecting());
		assertNotNull(service.vehicle);
		assertTrue(service.vehicle.id == vehicle.id);
		assertTrue(service.vehicle.indicative.equals(vehicle.indicative));
		assertNotNull(service.notificationDispatcher);
	}
}
package com.flightroster.passenger.config;

import com.flightroster.passenger.entity.Passenger;
import com.flightroster.passenger.entity.SeatType;
import com.flightroster.passenger.repository.PassengerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private PassengerRepository passengerRepository;

    @Override
    public void run(String... args) throws Exception {
        // Örnek yolcular - sadece yoksa ekle
        if (passengerRepository.findByPassengerId("PAX001").isEmpty()) {
            Passenger passenger1 = new Passenger();
            passenger1.setPassengerId("PAX001");
            passenger1.setFlightId("TK001");
            passenger1.setName("Ahmet Yılmaz");
            passenger1.setAge(35);
            passenger1.setGender("Male");
            passenger1.setNationality("Turkish");
            passenger1.setSeatType(SeatType.BUSINESS);
            passenger1.setSeatNumber("1A");
            passenger1.setIsInfant(false);
            passengerRepository.save(passenger1);
        }

        if (passengerRepository.findByPassengerId("PAX002").isEmpty()) {
            Passenger passenger2 = new Passenger();
            passenger2.setPassengerId("PAX002");
            passenger2.setFlightId("TK001");
            passenger2.setName("Ayşe Kaya");
            passenger2.setAge(28);
            passenger2.setGender("Female");
            passenger2.setNationality("Turkish");
            passenger2.setSeatType(SeatType.ECONOMY);
            passenger2.setSeatNumber("15B");
            passenger2.setIsInfant(false);
            passengerRepository.save(passenger2);
        }

        if (passengerRepository.findByPassengerId("PAX003").isEmpty()) {
            Passenger passenger3 = new Passenger();
            passenger3.setPassengerId("PAX003");
            passenger3.setFlightId("TK001");
            passenger3.setName("Mehmet Demir");
            passenger3.setAge(1);
            passenger3.setGender("Male");
            passenger3.setNationality("Turkish");
            passenger3.setSeatType(SeatType.ECONOMY);
            passenger3.setSeatNumber(null); // Infant, no seat
            passenger3.setIsInfant(true);
            passenger3.setParentPassengerId("PAX002");
            passengerRepository.save(passenger3);
        }

        if (passengerRepository.findByPassengerId("PAX004").isEmpty()) {
            Passenger passenger4 = new Passenger();
            passenger4.setPassengerId("PAX004");
            passenger4.setFlightId("TK002");
            passenger4.setName("John Smith");
            passenger4.setAge(42);
            passenger4.setGender("Male");
            passenger4.setNationality("British");
            passenger4.setSeatType(SeatType.BUSINESS);
            passenger4.setSeatNumber("2A");
            passenger4.setIsInfant(false);
            passengerRepository.save(passenger4);
        }

        if (passengerRepository.findByPassengerId("PAX005").isEmpty()) {
            Passenger passenger5 = new Passenger();
            passenger5.setPassengerId("PAX005");
            passenger5.setFlightId("TK002");
            passenger5.setName("Sarah Johnson");
            passenger5.setAge(38);
            passenger5.setGender("Female");
            passenger5.setNationality("American");
            passenger5.setSeatType(SeatType.ECONOMY);
            passenger5.setSeatNumber("20C");
            passenger5.setIsInfant(false);
            passengerRepository.save(passenger5);
        }

        if (passengerRepository.findByPassengerId("PAX006").isEmpty()) {
            Passenger passenger6 = new Passenger();
            passenger6.setPassengerId("PAX006");
            passenger6.setFlightId("TK002");
            passenger6.setName("Emma Wilson");
            passenger6.setAge(25);
            passenger6.setGender("Female");
            passenger6.setNationality("Canadian");
            passenger6.setSeatType(SeatType.ECONOMY);
            passenger6.setSeatNumber(null); // Not assigned yet
            passenger6.setIsInfant(false);
            passenger6.setAffiliatedPassengerIds("[\"PAX005\"]"); // Wants to sit near PAX005
            passengerRepository.save(passenger6);
        }

        // TK003, TK004, TK005 için daha fazla passenger ekle
        if (passengerRepository.findByPassengerId("PAX007").isEmpty()) {
            Passenger passenger7 = new Passenger();
            passenger7.setPassengerId("PAX007");
            passenger7.setFlightId("TK003");
            passenger7.setName("Maria Garcia");
            passenger7.setAge(30);
            passenger7.setGender("Female");
            passenger7.setNationality("Spanish");
            passenger7.setSeatType(SeatType.BUSINESS);
            passenger7.setSeatNumber(null);
            passenger7.setIsInfant(false);
            passengerRepository.save(passenger7);
        }

        if (passengerRepository.findByPassengerId("PAX008").isEmpty()) {
            Passenger passenger8 = new Passenger();
            passenger8.setPassengerId("PAX008");
            passenger8.setFlightId("TK003");
            passenger8.setName("Hans Mueller");
            passenger8.setAge(45);
            passenger8.setGender("Male");
            passenger8.setNationality("German");
            passenger8.setSeatType(SeatType.ECONOMY);
            passenger8.setSeatNumber(null);
            passenger8.setIsInfant(false);
            passengerRepository.save(passenger8);
        }

        if (passengerRepository.findByPassengerId("PAX009").isEmpty()) {
            Passenger passenger9 = new Passenger();
            passenger9.setPassengerId("PAX009");
            passenger9.setFlightId("TK004");
            passenger9.setName("Isabella Rossi");
            passenger9.setAge(28);
            passenger9.setGender("Female");
            passenger9.setNationality("Italian");
            passenger9.setSeatType(SeatType.BUSINESS);
            passenger9.setSeatNumber(null);
            passenger9.setIsInfant(false);
            passengerRepository.save(passenger9);
        }

        if (passengerRepository.findByPassengerId("PAX010").isEmpty()) {
            Passenger passenger10 = new Passenger();
            passenger10.setPassengerId("PAX010");
            passenger10.setFlightId("TK004");
            passenger10.setName("Ahmed Hassan");
            passenger10.setAge(35);
            passenger10.setGender("Male");
            passenger10.setNationality("Egyptian");
            passenger10.setSeatType(SeatType.ECONOMY);
            passenger10.setSeatNumber(null);
            passenger10.setIsInfant(false);
            passengerRepository.save(passenger10);
        }

        if (passengerRepository.findByPassengerId("PAX011").isEmpty()) {
            Passenger passenger11 = new Passenger();
            passenger11.setPassengerId("PAX011");
            passenger11.setFlightId("TK005");
            passenger11.setName("Elena Petrov");
            passenger11.setAge(32);
            passenger11.setGender("Female");
            passenger11.setNationality("Russian");
            passenger11.setSeatType(SeatType.BUSINESS);
            passenger11.setSeatNumber(null);
            passenger11.setIsInfant(false);
            passengerRepository.save(passenger11);
        }

        if (passengerRepository.findByPassengerId("PAX012").isEmpty()) {
            Passenger passenger12 = new Passenger();
            passenger12.setPassengerId("PAX012");
            passenger12.setFlightId("TK005");
            passenger12.setName("Carlos Mendez");
            passenger12.setAge(40);
            passenger12.setGender("Male");
            passenger12.setNationality("Mexican");
            passenger12.setSeatType(SeatType.ECONOMY);
            passenger12.setSeatNumber(null);
            passenger12.setIsInfant(false);
            passengerRepository.save(passenger12);
        }

        System.out.println("Örnek yolcu verileri yüklendi!");
    }
}

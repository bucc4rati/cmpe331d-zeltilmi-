package com.flightroster.pilot.config;

import com.flightroster.pilot.entity.Pilot;
import com.flightroster.pilot.entity.SeniorityLevel;
import com.flightroster.pilot.repository.PilotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private PilotRepository pilotRepository;

    @Override
    public void run(String... args) throws Exception {
        // Örnek pilotlar - sadece yoksa ekle
        if (pilotRepository.findByPilotId("P001").isEmpty()) {
            Pilot pilot1 = new Pilot();
            pilot1.setPilotId("P001");
            pilot1.setName("Ahmet Yılmaz");
            pilot1.setAge(35);
            pilot1.setGender("Male");
            pilot1.setNationality("Turkish");
            pilot1.setKnownLanguages("[\"Turkish\", \"English\"]");
            pilot1.setVehicleRestriction("Boeing 737-800");
            pilot1.setMaxDistanceKm(2000.0);
            pilot1.setSeniorityLevel(SeniorityLevel.SENIOR);
            pilot1.setIsAvailable(true);
            pilotRepository.save(pilot1);
        }

        if (pilotRepository.findByPilotId("P002").isEmpty()) {
            Pilot pilot2 = new Pilot();
            pilot2.setPilotId("P002");
            pilot2.setName("Mehmet Demir");
            pilot2.setAge(28);
            pilot2.setGender("Male");
            pilot2.setNationality("Turkish");
            pilot2.setKnownLanguages("[\"Turkish\", \"English\", \"German\"]");
            pilot2.setVehicleRestriction("Airbus A320");
            pilot2.setMaxDistanceKm(1500.0);
            pilot2.setSeniorityLevel(SeniorityLevel.JUNIOR);
            pilot2.setIsAvailable(true);
            pilotRepository.save(pilot2);
        }

        if (pilotRepository.findByPilotId("P003").isEmpty()) {
            Pilot pilot3 = new Pilot();
            pilot3.setPilotId("P003");
            pilot3.setName("Ayşe Kaya");
            pilot3.setAge(24);
            pilot3.setGender("Female");
            pilot3.setNationality("Turkish");
            pilot3.setKnownLanguages("[\"Turkish\", \"English\"]");
            pilot3.setVehicleRestriction("Boeing 737-800");
            pilot3.setMaxDistanceKm(1000.0);
            pilot3.setSeniorityLevel(SeniorityLevel.TRAINEE);
            pilot3.setIsAvailable(true);
            pilotRepository.save(pilot3);
        }

        if (pilotRepository.findByPilotId("P004").isEmpty()) {
            Pilot pilot4 = new Pilot();
            pilot4.setPilotId("P004");
            pilot4.setName("John Smith");
            pilot4.setAge(42);
            pilot4.setGender("Male");
            pilot4.setNationality("British");
            pilot4.setKnownLanguages("[\"English\", \"French\"]");
            pilot4.setVehicleRestriction("Airbus A320");
            pilot4.setMaxDistanceKm(3000.0);
            pilot4.setSeniorityLevel(SeniorityLevel.SENIOR);
            pilot4.setIsAvailable(true);
            pilotRepository.save(pilot4);
        }

        // Yeni pilotlar ekle
        if (pilotRepository.findByPilotId("P005").isEmpty()) {
            Pilot pilot5 = new Pilot();
            pilot5.setPilotId("P005");
            pilot5.setName("Maria Garcia");
            pilot5.setAge(38);
            pilot5.setGender("Female");
            pilot5.setNationality("Spanish");
            pilot5.setKnownLanguages("[\"Spanish\", \"English\", \"Italian\"]");
            pilot5.setVehicleRestriction("Boeing 737-800");
            pilot5.setMaxDistanceKm(2500.0);
            pilot5.setSeniorityLevel(SeniorityLevel.SENIOR);
            pilot5.setIsAvailable(true);
            pilotRepository.save(pilot5);
        }

        if (pilotRepository.findByPilotId("P006").isEmpty()) {
            Pilot pilot6 = new Pilot();
            pilot6.setPilotId("P006");
            pilot6.setName("Hans Mueller");
            pilot6.setAge(45);
            pilot6.setGender("Male");
            pilot6.setNationality("German");
            pilot6.setKnownLanguages("[\"German\", \"English\", \"French\"]");
            pilot6.setVehicleRestriction("Airbus A320");
            pilot6.setMaxDistanceKm(3500.0);
            pilot6.setSeniorityLevel(SeniorityLevel.SENIOR);
            pilot6.setIsAvailable(true);
            pilotRepository.save(pilot6);
        }

        if (pilotRepository.findByPilotId("P007").isEmpty()) {
            Pilot pilot7 = new Pilot();
            pilot7.setPilotId("P007");
            pilot7.setName("Fatma Özkan");
            pilot7.setAge(31);
            pilot7.setGender("Female");
            pilot7.setNationality("Turkish");
            pilot7.setKnownLanguages("[\"Turkish\", \"English\", \"Arabic\"]");
            pilot7.setVehicleRestriction("Boeing 737-800");
            pilot7.setMaxDistanceKm(1800.0);
            pilot7.setSeniorityLevel(SeniorityLevel.JUNIOR);
            pilot7.setIsAvailable(true);
            pilotRepository.save(pilot7);
        }

        if (pilotRepository.findByPilotId("P008").isEmpty()) {
            Pilot pilot8 = new Pilot();
            pilot8.setPilotId("P008");
            pilot8.setName("Pierre Dubois");
            pilot8.setAge(39);
            pilot8.setGender("Male");
            pilot8.setNationality("French");
            pilot8.setKnownLanguages("[\"French\", \"English\", \"Spanish\"]");
            pilot8.setVehicleRestriction("Airbus A320");
            pilot8.setMaxDistanceKm(2800.0);
            pilot8.setSeniorityLevel(SeniorityLevel.SENIOR);
            pilot8.setIsAvailable(true);
            pilotRepository.save(pilot8);
        }

        if (pilotRepository.findByPilotId("P009").isEmpty()) {
            Pilot pilot9 = new Pilot();
            pilot9.setPilotId("P009");
            pilot9.setName("Anna Kowalski");
            pilot9.setAge(26);
            pilot9.setGender("Female");
            pilot9.setNationality("Polish");
            pilot9.setKnownLanguages("[\"Polish\", \"English\", \"German\"]");
            pilot9.setVehicleRestriction("Boeing 737-800");
            pilot9.setMaxDistanceKm(1200.0);
            pilot9.setSeniorityLevel(SeniorityLevel.JUNIOR);
            pilot9.setIsAvailable(true);
            pilotRepository.save(pilot9);
        }

        if (pilotRepository.findByPilotId("P010").isEmpty()) {
            Pilot pilot10 = new Pilot();
            pilot10.setPilotId("P010");
            pilot10.setName("Roberto Silva");
            pilot10.setAge(33);
            pilot10.setGender("Male");
            pilot10.setNationality("Portuguese");
            pilot10.setKnownLanguages("[\"Portuguese\", \"English\", \"Spanish\"]");
            pilot10.setVehicleRestriction("Airbus A320");
            pilot10.setMaxDistanceKm(2200.0);
            pilot10.setSeniorityLevel(SeniorityLevel.JUNIOR);
            pilot10.setIsAvailable(true);
            pilotRepository.save(pilot10);
        }

        // Boeing 777-300ER pilotları kaldırıldı

        // Airbus A330 pilotları ekle
        if (pilotRepository.findByPilotId("P013").isEmpty()) {
            Pilot pilot13 = new Pilot();
            pilot13.setPilotId("P013");
            pilot13.setName("Captain Michael Brown");
            pilot13.setAge(41);
            pilot13.setGender("Male");
            pilot13.setNationality("British");
            pilot13.setKnownLanguages("[\"English\", \"German\"]");
            pilot13.setVehicleRestriction("Airbus A330");
            pilot13.setMaxDistanceKm(6000.0);
            pilot13.setSeniorityLevel(SeniorityLevel.SENIOR);
            pilot13.setIsAvailable(true);
            pilotRepository.save(pilot13);
        }

        if (pilotRepository.findByPilotId("P014").isEmpty()) {
            Pilot pilot14 = new Pilot();
            pilot14.setPilotId("P014");
            pilot14.setName("Captain Lisa Davis");
            pilot14.setAge(37);
            pilot14.setGender("Female");
            pilot14.setNationality("Australian");
            pilot14.setKnownLanguages("[\"English\", \"Japanese\"]");
            pilot14.setVehicleRestriction("Airbus A330");
            pilot14.setMaxDistanceKm(5500.0);
            pilot14.setSeniorityLevel(SeniorityLevel.JUNIOR);
            pilot14.setIsAvailable(true);
            pilotRepository.save(pilot14);
        }

        // Boeing 737-800 için ek pilotlar (4 flight için yeterli sayıda)
        String[] b737Names = {"Mustafa Arslan", "Emre Çelik", "Deniz Yıldız", "Burak Kılıç", "Cem Özdemir", 
                               "Selin Aydın", "Gizem Şen", "Kemal Doğan", "Berkay Yavuz", "Ece Demir",
                               "Onur Aslan", "Dilara Koç", "Tolga Öztürk", "Seda Yılmaz", "Caner Şahin",
                               "Melis Aktaş", "Kerem Özkan", "Elif Kaya", "Arda Çetin", "Zeynep Arı"};
        String[] b737Nationalities = {"Turkish", "Turkish", "Turkish", "Turkish", "Turkish",
                                      "Turkish", "Turkish", "Turkish", "Turkish", "Turkish",
                                      "Turkish", "Turkish", "Turkish", "Turkish", "Turkish",
                                      "Turkish", "Turkish", "Turkish", "Turkish", "Turkish"};
        SeniorityLevel[] b737Levels = {SeniorityLevel.SENIOR, SeniorityLevel.SENIOR, SeniorityLevel.SENIOR, SeniorityLevel.JUNIOR, SeniorityLevel.JUNIOR,
                                       SeniorityLevel.SENIOR, SeniorityLevel.JUNIOR, SeniorityLevel.SENIOR, SeniorityLevel.JUNIOR, SeniorityLevel.JUNIOR,
                                       SeniorityLevel.SENIOR, SeniorityLevel.JUNIOR, SeniorityLevel.SENIOR, SeniorityLevel.JUNIOR, SeniorityLevel.TRAINEE,
                                       SeniorityLevel.JUNIOR, SeniorityLevel.SENIOR, SeniorityLevel.JUNIOR, SeniorityLevel.SENIOR, SeniorityLevel.JUNIOR};
        double[] b737Distances = {3000.0, 2800.0, 2500.0, 2000.0, 1800.0,
                                  3200.0, 2400.0, 2900.0, 2100.0, 1900.0,
                                  3100.0, 2200.0, 2700.0, 2300.0, 1500.0,
                                  2600.0, 3000.0, 2000.0, 2800.0, 2400.0};
        
        for (int i = 0; i < 20; i++) {
            String pilotId = "P" + String.format("%03d", 15 + i);
            if (pilotRepository.findByPilotId(pilotId).isEmpty()) {
                Pilot pilot = new Pilot();
                pilot.setPilotId(pilotId);
                pilot.setName(b737Names[i]);
                pilot.setAge(28 + (i % 15));
                pilot.setGender(i % 3 == 0 ? "Female" : "Male");
                pilot.setNationality(b737Nationalities[i]);
                pilot.setKnownLanguages("[\"Turkish\", \"English\"]");
                pilot.setVehicleRestriction("Boeing 737-800");
                pilot.setMaxDistanceKm(b737Distances[i]);
                pilot.setSeniorityLevel(b737Levels[i]);
                pilot.setIsAvailable(true);
                pilotRepository.save(pilot);
            }
        }

        // Airbus A320 için ek pilotlar (4 flight için yeterli sayıda)
        String[] a320Names = {"Thomas Anderson", "Emma Wilson", "David Martinez", "Sophie Laurent", "Marco Rossi",
                              "Anna Schmidt", "James Taylor", "Laura Garcia", "Michael Brown", "Isabella Silva",
                              "Robert Johnson", "Maria Fernandez", "William Lee", "Elena Petrov", "Daniel Kim",
                              "Olivia White", "Christopher Green", "Amelia Black", "Matthew Blue", "Charlotte Red"};
        String[] a320Nationalities = {"American", "British", "Spanish", "French", "Italian",
                                      "German", "American", "Spanish", "American", "Portuguese",
                                      "American", "Spanish", "American", "Russian", "Korean",
                                      "British", "American", "British", "American", "British"};
        SeniorityLevel[] a320Levels = {SeniorityLevel.SENIOR, SeniorityLevel.SENIOR, SeniorityLevel.JUNIOR, SeniorityLevel.SENIOR, SeniorityLevel.JUNIOR,
                                       SeniorityLevel.SENIOR, SeniorityLevel.JUNIOR, SeniorityLevel.JUNIOR, SeniorityLevel.SENIOR, SeniorityLevel.JUNIOR,
                                       SeniorityLevel.SENIOR, SeniorityLevel.JUNIOR, SeniorityLevel.SENIOR, SeniorityLevel.JUNIOR, SeniorityLevel.TRAINEE,
                                       SeniorityLevel.JUNIOR, SeniorityLevel.SENIOR, SeniorityLevel.JUNIOR, SeniorityLevel.SENIOR, SeniorityLevel.JUNIOR};
        double[] a320Distances = {3500.0, 3300.0, 3000.0, 3200.0, 2800.0,
                                 3600.0, 2900.0, 3100.0, 3400.0, 2700.0,
                                 3500.0, 3000.0, 3300.0, 2900.0, 2000.0,
                                 3100.0, 3400.0, 3000.0, 3200.0, 2800.0};
        
        for (int i = 0; i < 20; i++) {
            String pilotId = "P" + String.format("%03d", 35 + i);
            if (pilotRepository.findByPilotId(pilotId).isEmpty()) {
                Pilot pilot = new Pilot();
                pilot.setPilotId(pilotId);
                pilot.setName(a320Names[i]);
                pilot.setAge(30 + (i % 18));
                pilot.setGender(i % 2 == 0 ? "Female" : "Male");
                pilot.setNationality(a320Nationalities[i]);
                pilot.setKnownLanguages("[\"English\", \"Spanish\", \"French\"]");
                pilot.setVehicleRestriction("Airbus A320");
                pilot.setMaxDistanceKm(a320Distances[i]);
                pilot.setSeniorityLevel(a320Levels[i]);
                pilot.setIsAvailable(true);
                pilotRepository.save(pilot);
            }
        }

        // Boeing 777-300ER pilotları kaldırıldı (P055-P064)

        // Airbus A330 için ek pilotlar (2 flight için yeterli sayıda)
        String[] a330Names = {"Captain Andreas Schmidt", "Captain Marie Dubois", "Captain Giovanni Romano",
                              "Captain Sarah O'Connor", "Captain Lars Johansson", "Captain Elena Vasquez",
                              "Captain Jean-Pierre Martin", "Captain Ingrid Bergman", "Captain Carlos Rodriguez",
                              "Captain Anna Kowalczyk"};
        String[] a330Nationalities = {"German", "French", "Italian", "Irish", "Swedish",
                                      "Spanish", "French", "Swedish", "Spanish", "Polish"};
        SeniorityLevel[] a330Levels = {SeniorityLevel.SENIOR, SeniorityLevel.SENIOR, SeniorityLevel.SENIOR, SeniorityLevel.SENIOR, SeniorityLevel.SENIOR,
                                       SeniorityLevel.JUNIOR, SeniorityLevel.SENIOR, SeniorityLevel.SENIOR, SeniorityLevel.JUNIOR, SeniorityLevel.JUNIOR};
        double[] a330Distances = {7000.0, 6800.0, 6500.0, 7200.0, 6900.0,
                                 6000.0, 7100.0, 6700.0, 6200.0, 6400.0};
        
        for (int i = 0; i < 10; i++) {
            String pilotId = "P" + String.format("%03d", 65 + i);
            if (pilotRepository.findByPilotId(pilotId).isEmpty()) {
                Pilot pilot = new Pilot();
                pilot.setPilotId(pilotId);
                pilot.setName(a330Names[i]);
                pilot.setAge(38 + (i % 12));
                pilot.setGender(i % 2 == 0 ? "Male" : "Female");
                pilot.setNationality(a330Nationalities[i]);
                pilot.setKnownLanguages("[\"English\", \"French\", \"German\"]");
                pilot.setVehicleRestriction("Airbus A330");
                pilot.setMaxDistanceKm(a330Distances[i]);
                pilot.setSeniorityLevel(a330Levels[i]);
                pilot.setIsAvailable(true);
                pilotRepository.save(pilot);
            }
        }

        System.out.println("Örnek pilot verileri yüklendi! Toplam: ~75 pilot");
    }
}

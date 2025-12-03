package com.flightroster.cabin.config;

import com.flightroster.cabin.entity.Attendant;
import com.flightroster.cabin.entity.AttendantType;
import com.flightroster.cabin.repository.AttendantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private AttendantRepository attendantRepository;

    @Override
    public void run(String... args) throws Exception {
        // Örnek kabin ekibi - sadece yoksa ekle
        if (attendantRepository.findByAttendantId("A001").isEmpty()) {
            Attendant attendant1 = new Attendant();
            attendant1.setAttendantId("A001");
            attendant1.setName("Fatma Yılmaz");
            attendant1.setAge(32);
            attendant1.setGender("Female");
            attendant1.setNationality("Turkish");
            attendant1.setKnownLanguages("[\"Turkish\", \"English\", \"French\"]");
            attendant1.setAttendantType(AttendantType.CHIEF);
            attendant1.setVehicleRestrictions("[\"Boeing 737-800\", \"Airbus A320\"]");
            attendant1.setIsAvailable(true);
            attendantRepository.save(attendant1);
        }

        if (attendantRepository.findByAttendantId("A002").isEmpty()) {
            Attendant attendant2 = new Attendant();
            attendant2.setAttendantId("A002");
            attendant2.setName("Mehmet Kaya");
            attendant2.setAge(28);
            attendant2.setGender("Male");
            attendant2.setNationality("Turkish");
            attendant2.setKnownLanguages("[\"Turkish\", \"English\"]");
            attendant2.setAttendantType(AttendantType.REGULAR);
            attendant2.setVehicleRestrictions("[\"Boeing 737-800\", \"Airbus A320\"]");
            attendant2.setIsAvailable(true);
            attendantRepository.save(attendant2);
        }

        if (attendantRepository.findByAttendantId("A003").isEmpty()) {
            Attendant attendant3 = new Attendant();
            attendant3.setAttendantId("A003");
            attendant3.setName("Ayşe Demir");
            attendant3.setAge(26);
            attendant3.setGender("Female");
            attendant3.setNationality("Turkish");
            attendant3.setKnownLanguages("[\"Turkish\", \"English\", \"German\"]");
            attendant3.setAttendantType(AttendantType.REGULAR);
            attendant3.setVehicleRestrictions("[\"Airbus A320\"]");
            attendant3.setIsAvailable(true);
            attendantRepository.save(attendant3);
        }

        if (attendantRepository.findByAttendantId("A004").isEmpty()) {
            Attendant attendant4 = new Attendant();
            attendant4.setAttendantId("A004");
            attendant4.setName("Ali Özkan");
            attendant4.setAge(35);
            attendant4.setGender("Male");
            attendant4.setNationality("Turkish");
            attendant4.setKnownLanguages("[\"Turkish\", \"English\"]");
            attendant4.setAttendantType(AttendantType.CHEF);
            attendant4.setVehicleRestrictions("[\"Boeing 737-800\", \"Airbus A320\"]");
            attendant4.setIsAvailable(true);
            attendant4.setRecipeTypes("[\"Turkish Cuisine\", \"Mediterranean\", \"Vegetarian\"]");
            attendantRepository.save(attendant4);
        }

        if (attendantRepository.findByAttendantId("A005").isEmpty()) {
            Attendant attendant5 = new Attendant();
            attendant5.setAttendantId("A005");
            attendant5.setName("Zeynep Şahin");
            attendant5.setAge(30);
            attendant5.setGender("Female");
            attendant5.setNationality("Turkish");
            attendant5.setKnownLanguages("[\"Turkish\", \"English\", \"French\"]");
            attendant5.setAttendantType(AttendantType.CHEF);
            attendant5.setVehicleRestrictions("[\"Boeing 737-800\"]");
            attendant5.setIsAvailable(true);
            attendant5.setRecipeTypes("[\"International Cuisine\", \"Asian Fusion\", \"Healthy Options\"]");
            attendantRepository.save(attendant5);
        }

        if (attendantRepository.findByAttendantId("A006").isEmpty()) {
            Attendant attendant6 = new Attendant();
            attendant6.setAttendantId("A006");
            attendant6.setName("Can Yıldız");
            attendant6.setAge(29);
            attendant6.setGender("Male");
            attendant6.setNationality("Turkish");
            attendant6.setKnownLanguages("[\"Turkish\", \"English\"]");
            attendant6.setAttendantType(AttendantType.REGULAR);
            attendant6.setVehicleRestrictions("[\"Boeing 737-800\", \"Airbus A320\"]");
            attendant6.setIsAvailable(true);
            attendantRepository.save(attendant6);
        }

        // Yeni kabin ekibi üyeleri ekle
        if (attendantRepository.findByAttendantId("A007").isEmpty()) {
            Attendant attendant7 = new Attendant();
            attendant7.setAttendantId("A007");
            attendant7.setName("Sarah Johnson");
            attendant7.setAge(27);
            attendant7.setGender("Female");
            attendant7.setNationality("American");
            attendant7.setKnownLanguages("[\"English\", \"Spanish\"]");
            attendant7.setAttendantType(AttendantType.REGULAR);
            attendant7.setVehicleRestrictions("[\"Boeing 737-800\", \"Airbus A320\"]");
            attendant7.setIsAvailable(true);
            attendantRepository.save(attendant7);
        }

        if (attendantRepository.findByAttendantId("A008").isEmpty()) {
            Attendant attendant8 = new Attendant();
            attendant8.setAttendantId("A008");
            attendant8.setName("Klaus Weber");
            attendant8.setAge(34);
            attendant8.setGender("Male");
            attendant8.setNationality("German");
            attendant8.setKnownLanguages("[\"German\", \"English\", \"French\"]");
            attendant8.setAttendantType(AttendantType.CHIEF);
            attendant8.setVehicleRestrictions("[\"Airbus A320\"]");
            attendant8.setIsAvailable(true);
            attendantRepository.save(attendant8);
        }

        if (attendantRepository.findByAttendantId("A009").isEmpty()) {
            Attendant attendant9 = new Attendant();
            attendant9.setAttendantId("A009");
            attendant9.setName("Isabella Rossi");
            attendant9.setAge(31);
            attendant9.setGender("Female");
            attendant9.setNationality("Italian");
            attendant9.setKnownLanguages("[\"Italian\", \"English\", \"French\"]");
            attendant9.setAttendantType(AttendantType.CHEF);
            attendant9.setVehicleRestrictions("[\"Boeing 737-800\", \"Airbus A320\"]");
            attendant9.setIsAvailable(true);
            attendant9.setRecipeTypes("[\"Italian Cuisine\", \"Mediterranean\", \"Fine Dining\"]");
            attendantRepository.save(attendant9);
        }

        if (attendantRepository.findByAttendantId("A010").isEmpty()) {
            Attendant attendant10 = new Attendant();
            attendant10.setAttendantId("A010");
            attendant10.setName("Ahmed Hassan");
            attendant10.setAge(33);
            attendant10.setGender("Male");
            attendant10.setNationality("Egyptian");
            attendant10.setKnownLanguages("[\"Arabic\", \"English\", \"French\"]");
            attendant10.setAttendantType(AttendantType.REGULAR);
            attendant10.setVehicleRestrictions("[\"Boeing 737-800\", \"Airbus A320\"]");
            attendant10.setIsAvailable(true);
            attendantRepository.save(attendant10);
        }

        if (attendantRepository.findByAttendantId("A011").isEmpty()) {
            Attendant attendant11 = new Attendant();
            attendant11.setAttendantId("A011");
            attendant11.setName("Elena Petrov");
            attendant11.setAge(28);
            attendant11.setGender("Female");
            attendant11.setNationality("Russian");
            attendant11.setKnownLanguages("[\"Russian\", \"English\", \"German\"]");
            attendant11.setAttendantType(AttendantType.REGULAR);
            attendant11.setVehicleRestrictions("[\"Airbus A320\"]");
            attendant11.setIsAvailable(true);
            attendantRepository.save(attendant11);
        }

        if (attendantRepository.findByAttendantId("A012").isEmpty()) {
            Attendant attendant12 = new Attendant();
            attendant12.setAttendantId("A012");
            attendant12.setName("Carlos Mendez");
            attendant12.setAge(36);
            attendant12.setGender("Male");
            attendant12.setNationality("Mexican");
            attendant12.setKnownLanguages("[\"Spanish\", \"English\"]");
            attendant12.setAttendantType(AttendantType.CHEF);
            attendant12.setVehicleRestrictions("[\"Boeing 737-800\"]");
            attendant12.setIsAvailable(true);
            attendant12.setRecipeTypes("[\"Mexican Cuisine\", \"Latin American\", \"Spicy Dishes\"]");
            attendantRepository.save(attendant12);
        }

        if (attendantRepository.findByAttendantId("A013").isEmpty()) {
            Attendant attendant13 = new Attendant();
            attendant13.setAttendantId("A013");
            attendant13.setName("Yuki Tanaka");
            attendant13.setAge(25);
            attendant13.setGender("Female");
            attendant13.setNationality("Japanese");
            attendant13.setKnownLanguages("[\"Japanese\", \"English\"]");
            attendant13.setAttendantType(AttendantType.REGULAR);
            attendant13.setVehicleRestrictions("[\"Boeing 737-800\", \"Airbus A320\"]");
            attendant13.setIsAvailable(true);
            attendantRepository.save(attendant13);
        }

        if (attendantRepository.findByAttendantId("A014").isEmpty()) {
            Attendant attendant14 = new Attendant();
            attendant14.setAttendantId("A014");
            attendant14.setName("Mohammed Al-Rashid");
            attendant14.setAge(40);
            attendant14.setGender("Male");
            attendant14.setNationality("Saudi Arabian");
            attendant14.setKnownLanguages("[\"Arabic\", \"English\", \"French\"]");
            attendant14.setAttendantType(AttendantType.CHIEF);
            attendant14.setVehicleRestrictions("[\"Boeing 737-800\", \"Airbus A320\"]");
            attendant14.setIsAvailable(true);
            attendantRepository.save(attendant14);
        }

        if (attendantRepository.findByAttendantId("A015").isEmpty()) {
            Attendant attendant15 = new Attendant();
            attendant15.setAttendantId("A015");
            attendant15.setName("Sophie Martin");
            attendant15.setAge(29);
            attendant15.setGender("Female");
            attendant15.setNationality("French");
            attendant15.setKnownLanguages("[\"French\", \"English\", \"Spanish\"]");
            attendant15.setAttendantType(AttendantType.REGULAR);
            attendant15.setVehicleRestrictions("[\"Airbus A320\"]");
            attendant15.setIsAvailable(true);
            attendantRepository.save(attendant15);
        }

        // Boeing 737-800 ve Airbus A320 için CHIEF attendants (küçük uçaklar için) - HER İKİSİ İÇİN YETERLİ
        String[] chiefNames = {"Ayşe Yıldız", "Mehmet Özkan", "Fatma Demir", "Ali Şahin", "Zeynep Kaya",
                               "Can Arslan", "Selin Çelik", "Burak Yavuz", "Dilara Aktaş", "Emre Doğan",
                               "Gizem Öztürk", "Kemal Aslan", "Ece Koç", "Onur Yılmaz", "Melis Şen",
                               "Kerem Arı", "Elif Çetin", "Arda Kılıç", "Tolga Özdemir", "Seda Aydın",
                               "Berkay Yavuz", "Deniz Koç", "Cem Arslan", "Selin Yıldız", "Burak Demir",
                               "Captain Emily Watson", "Captain David Miller", "Captain Lisa Anderson", 
                               "Captain Mark Thompson", "Captain Jennifer White", "Captain Robert Harris",
                               "Captain Sarah Davis", "Captain Michael Johnson", "Captain Amanda Brown",
                               "Captain James Wilson", "Captain Nicole Martinez", "Captain Kevin Taylor",
                               "Captain Rachel Green", "Captain Daniel Lee", "Captain Michelle Clark",
                               "Captain Brian Adams", "Captain Jessica Moore", "Captain Steven Young",
                               "Captain Ashley King", "Captain Ryan Scott", "Captain Lauren Hill",
                               "Captain Jason Wright", "Captain Megan Turner", "Captain Justin Baker",
                               "Captain Kimberly Hall", "Captain Brandon Evans", "Captain Stephanie Phillips"};
        String[] chiefNationalities = new String[50];
        for (int i = 0; i < 50; i++) {
            if (i < 25) {
                chiefNationalities[i] = i < 15 ? "Turkish" : (i < 20 ? "British" : "German");
            } else {
                chiefNationalities[i] = i < 35 ? "American" : (i < 42 ? "British" : "Canadian");
            }
        }
        
        for (int i = 0; i < 50; i++) {
            String attendantId = "A" + String.format("%03d", 16 + i);
            try {
                Optional<Attendant> existing = attendantRepository.findByAttendantId(attendantId);
                Attendant attendant;
                if (existing.isPresent()) {
                    // Update existing record
                    attendant = existing.get();
                } else {
                    // Create new record
                    attendant = new Attendant();
                    attendant.setAttendantId(attendantId);
                }
                attendant.setName(chiefNames[i]);
                attendant.setAge(30 + (i % 15));
                attendant.setGender(i % 2 == 0 ? "Female" : "Male");
                attendant.setNationality(chiefNationalities[i]);
                attendant.setKnownLanguages("[\"Turkish\", \"English\", \"French\"]");
                attendant.setAttendantType(AttendantType.CHIEF);
                attendant.setVehicleRestrictions("[\"Boeing 737-800\", \"Airbus A320\"]");
                attendant.setIsAvailable(true);
                attendantRepository.save(attendant);
            } catch (Exception e) {
                System.err.println("Warning: Could not save CHIEF " + attendantId + ": " + e.getMessage());
            }
        }

        // Boeing 737-800 ve Airbus A320 için REGULAR attendants (küçük uçaklar için) - HER İKİSİ İÇİN YETERLİ
        String[] regularNames = {"Ahmet Kılıç", "Ayşe Özkan", "Mehmet Yılmaz", "Fatma Şahin", "Ali Demir",
                                 "Zeynep Kaya", "Can Arslan", "Selin Çelik", "Burak Yavuz", "Dilara Aktaş",
                                 "Emre Doğan", "Gizem Öztürk", "Kemal Aslan", "Ece Koç", "Onur Yılmaz",
                                 "Melis Şen", "Kerem Arı", "Elif Çetin", "Arda Kılıç", "Tolga Özdemir",
                                 "Seda Aydın", "Berkay Yavuz", "Deniz Koç", "Cem Arslan", "Selin Yıldız",
                                 "Burak Demir", "Sarah Johnson", "Michael Brown", "Emma Wilson", "David Lee",
                                 "Laura Garcia", "James Taylor", "Sophie Martin", "Thomas Anderson", "Maria Silva",
                                 "Robert Kim", "Anna Schmidt", "Christopher Green", "Isabella Rossi", "Daniel Park",
                                 "Olivia White", "Matthew Blue", "Charlotte Red", "William Black", "Amelia Gray",
                                 "Ethan Brown", "Mia Johnson", "Noah Davis", "Ava Miller", "Liam Wilson",
                                 "Sophia Martinez", "Mason Taylor", "Emily Anderson", "Logan Thomas", "Harper Jackson",
                                 "Aiden White", "Ella Harris", "Lucas Martin", "Grace Lee", "Alexander Young",
                                 "Chloe King", "Benjamin Scott", "Lily Green", "Henry Adams", "Zoe Baker",
                                 "Jack Hill", "Nora Carter", "Owen Mitchell", "Ruby Turner", "Samuel Phillips",
                                 "Emma Thompson", "Oliver Davis", "Sophia Wilson", "Noah Martinez", "Isabella Taylor",
                                 "Lucas Anderson", "Olivia Brown", "Mason Garcia", "Ava Rodriguez", "Ethan Lee",
                                 "Mia White", "Liam Harris", "Charlotte Clark", "Benjamin Lewis", "Amelia Walker",
                                 "James Hall", "Harper Young", "Alexander King", "Evelyn Wright", "Daniel Lopez",
                                 "Abigail Hill", "Matthew Green", "Elizabeth Adams", "Joseph Baker", "Sofia Nelson",
                                 "David Carter", "Madison Mitchell", "Andrew Roberts", "Chloe Turner", "Ryan Phillips",
                                 "Layla Campbell", "Nathan Parker", "Aria Evans", "Kevin Edwards", "Samantha Collins",
                                 "Justin Stewart", "Avery Sanchez", "Brandon Morris", "Lily Rogers", "Tyler Reed",
                                 "Victoria Chen", "Ryan Kim", "Sophia Patel", "Michael Zhang", "Emma Rodriguez",
                                 "Daniel Singh", "Isabella Kumar", "James Wang", "Olivia Fernandez", "Noah Gupta"};
        String[] regularNationalities = new String[120];
        for (int i = 0; i < 120; i++) {
            if (i < 30) regularNationalities[i] = "Turkish";
            else if (i < 60) regularNationalities[i] = "American";
            else if (i < 85) regularNationalities[i] = "British";
            else if (i < 105) regularNationalities[i] = "German";
            else regularNationalities[i] = "French";
        }
        
        for (int i = 0; i < 120; i++) {
            String attendantId = "A" + String.format("%03d", 66 + i);
            if (attendantRepository.findByAttendantId(attendantId).isEmpty()) {
                Attendant attendant = new Attendant();
                attendant.setAttendantId(attendantId);
                attendant.setName(regularNames[i]);
                attendant.setAge(22 + (i % 18));
                attendant.setGender(i % 2 == 0 ? "Female" : "Male");
                attendant.setNationality(regularNationalities[i]);
                attendant.setKnownLanguages("[\"English\", \"Turkish\", \"French\"]");
                attendant.setAttendantType(AttendantType.REGULAR);
                attendant.setVehicleRestrictions("[\"Boeing 737-800\", \"Airbus A320\"]");
                attendant.setIsAvailable(true);
                attendantRepository.save(attendant);
            }
        }

        // Boeing 737-800 ve Airbus A320 için CHEF attendants - HER İKİSİ İÇİN YETERLİ
        String[] chefNames = {"Ali Özkan", "Zeynep Şahin", "Mehmet Yıldız", "Fatma Kaya", "Can Demir",
                             "Selin Arslan", "Burak Çelik", "Dilara Yavuz", "Emre Aktaş", "Gizem Doğan",
                             "Kemal Öztürk", "Ece Aslan", "Onur Koç", "Melis Yılmaz", "Kerem Şen",
                             "Chef Marco Bianchi", "Chef Sofia Petrov", "Chef Jean-Luc Moreau", "Chef Elena Vasquez",
                             "Chef Carlos Rodriguez", "Chef Anna Kowalczyk", "Chef Sebastian Müller", "Chef Natalia Kowalski",
                             "Chef Ahmed Hassan", "Chef Yuki Tanaka", "Chef Li Wei", "Chef Maria Santos",
                             "Chef Pierre Dubois", "Chef Isabella Romano", "Chef Hans Schmidt", "Chef Fatima Al-Mansoori"};
        String[] chefNationalities = new String[30];
        for (int i = 0; i < 30; i++) {
            if (i < 15) chefNationalities[i] = "Turkish";
            else if (i < 20) chefNationalities[i] = "Italian";
            else if (i < 25) chefNationalities[i] = "French";
            else chefNationalities[i] = i < 27 ? "German" : (i < 29 ? "Spanish" : "Japanese");
        }
        
        for (int i = 0; i < 30; i++) {
            String attendantId = "A" + String.format("%03d", 186 + i);
            if (attendantRepository.findByAttendantId(attendantId).isEmpty()) {
                Attendant attendant = new Attendant();
                attendant.setAttendantId(attendantId);
                attendant.setName(chefNames[i]);
                attendant.setAge(28 + (i % 12));
                attendant.setGender(i % 2 == 0 ? "Male" : "Female");
                attendant.setNationality(chefNationalities[i]);
                attendant.setKnownLanguages("[\"Turkish\", \"English\", \"French\"]");
                attendant.setAttendantType(AttendantType.CHEF);
                attendant.setVehicleRestrictions("[\"Boeing 737-800\", \"Airbus A320\"]");
                attendant.setIsAvailable(true);
                attendant.setRecipeTypes("[\"Turkish Cuisine\", \"Mediterranean\", \"International\", \"Vegetarian\"]");
                attendantRepository.save(attendant);
            }
        }

        // Boeing 777-300ER cabin crew kaldırıldı

        // Boeing 777-300ER REGULAR ve CHEF attendants kaldırıldı

        // Airbus A330 için CHIEF attendants
        String[] a330ChiefNames = {"Captain Andreas Schmidt", "Captain Marie Dubois", "Captain Giovanni Romano",
                                   "Captain Sarah O'Connor", "Captain Lars Johansson", "Captain Elena Vasquez",
                                   "Captain Jean-Pierre Martin", "Captain Ingrid Bergman"};
        String[] a330ChiefNationalities = {"German", "French", "Italian", "Irish", "Swedish", "Spanish", "French", "Swedish"};
        
        for (int i = 0; i < 8; i++) {
            String attendantId = "A" + String.format("%03d", 177 + i);
            if (attendantRepository.findByAttendantId(attendantId).isEmpty()) {
                Attendant attendant = new Attendant();
                attendant.setAttendantId(attendantId);
                attendant.setName(a330ChiefNames[i]);
                attendant.setAge(33 + (i % 12));
                attendant.setGender(i % 2 == 0 ? "Male" : "Female");
                attendant.setNationality(a330ChiefNationalities[i]);
                attendant.setKnownLanguages("[\"English\", \"French\", \"German\", \"Spanish\"]");
                attendant.setAttendantType(AttendantType.CHIEF);
                attendant.setVehicleRestrictions("[\"Airbus A330\"]");
                attendant.setIsAvailable(true);
                attendantRepository.save(attendant);
            }
        }

        // Airbus A330 için REGULAR attendants
        String[] a330RegularNames = {"Sophie Laurent", "Marco Rossi", "Anna Schmidt", "Laura Garcia", "Michael Brown",
                                     "Isabella Silva", "Robert Johnson", "Maria Fernandez", "William Lee", "Elena Petrov",
                                     "Daniel Kim", "Olivia White", "Christopher Green", "Amelia Black", "Matthew Blue",
                                     "Charlotte Red", "James Taylor", "Emma Wilson", "David Martinez", "Sophie Martin",
                                     "Thomas Anderson", "Laura Garcia", "Michael Brown", "Isabella Silva", "Robert Johnson",
                                     "Maria Fernandez", "William Lee", "Elena Petrov", "Daniel Kim", "Olivia White"};
        String[] a330RegularNationalities = new String[30];
        for (int i = 0; i < 30; i++) {
            if (i < 10) a330RegularNationalities[i] = "French";
            else if (i < 18) a330RegularNationalities[i] = "German";
            else if (i < 24) a330RegularNationalities[i] = "Spanish";
            else a330RegularNationalities[i] = "Italian";
        }
        
        for (int i = 0; i < 30; i++) {
            String attendantId = "A" + String.format("%03d", 216 + i);
            if (attendantRepository.findByAttendantId(attendantId).isEmpty()) {
                Attendant attendant = new Attendant();
                attendant.setAttendantId(attendantId);
                attendant.setName(a330RegularNames[i]);
                attendant.setAge(25 + (i % 15));
                attendant.setGender(i % 2 == 0 ? "Female" : "Male");
                attendant.setNationality(a330RegularNationalities[i]);
                attendant.setKnownLanguages("[\"English\", \"French\", \"German\", \"Spanish\"]");
                attendant.setAttendantType(AttendantType.REGULAR);
                attendant.setVehicleRestrictions("[\"Airbus A330\"]");
                attendant.setIsAvailable(true);
                attendantRepository.save(attendant);
            }
        }

        // Airbus A330 için CHEF attendants
        String[] a330ChefNames = {"Chef Carlos Rodriguez", "Chef Anna Kowalczyk", "Chef Sebastian Müller",
                                  "Chef Natalia Kowalski", "Chef Jean-Luc Moreau", "Chef Elena Vasquez",
                                  "Chef Marco Bianchi", "Chef Ingrid Bergman"};
        String[] a330ChefNationalities = {"Spanish", "Polish", "German", "Polish", "French", "Spanish", "Italian", "Swedish"};
        
        for (int i = 0; i < 8; i++) {
            String attendantId = "A" + String.format("%03d", 246 + i);
            if (attendantRepository.findByAttendantId(attendantId).isEmpty()) {
                Attendant attendant = new Attendant();
                attendant.setAttendantId(attendantId);
                attendant.setName(a330ChefNames[i]);
                attendant.setAge(30 + (i % 11));
                attendant.setGender(i % 2 == 0 ? "Male" : "Female");
                attendant.setNationality(a330ChefNationalities[i]);
                attendant.setKnownLanguages("[\"English\", \"French\", \"German\", \"Spanish\"]");
                attendant.setAttendantType(AttendantType.CHEF);
                attendant.setVehicleRestrictions("[\"Airbus A330\"]");
                attendant.setIsAvailable(true);
                attendant.setRecipeTypes("[\"European Cuisine\", \"Mediterranean\", \"Fine Dining\", \"Vegetarian Options\"]");
                attendantRepository.save(attendant);
            }
        }

        // EK CHIEF'LER - Her uçak tipi için yeterli sayıda (12 flight için)
        // Boeing 737-800 & Airbus A320 için ek CHIEF'ler (A300'den başla)
        String[] extraChiefNames737 = {"Chief Attendant Emma Wilson", "Chief Attendant David Martinez", "Chief Attendant Laura Garcia",
                                      "Chief Attendant James Taylor", "Chief Attendant Sophie Martin", "Chief Attendant Thomas Anderson",
                                      "Chief Attendant Maria Silva", "Chief Attendant Robert Kim", "Chief Attendant Anna Schmidt",
                                      "Chief Attendant Christopher Green", "Chief Attendant Isabella Rossi", "Chief Attendant Daniel Park",
                                      "Chief Attendant Olivia White", "Chief Attendant Matthew Blue", "Chief Attendant Charlotte Red",
                                      "Chief Attendant William Black", "Chief Attendant Amelia Gray", "Chief Attendant Ethan Brown",
                                      "Chief Attendant Mia Johnson", "Chief Attendant Noah Davis", "Chief Attendant Ava Miller",
                                      "Chief Attendant Liam Wilson", "Chief Attendant Sophia Martinez", "Chief Attendant Mason Taylor",
                                      "Chief Attendant Emily Anderson", "Chief Attendant Logan Thomas", "Chief Attendant Harper Jackson",
                                      "Chief Attendant Aiden White", "Chief Attendant Ella Harris", "Chief Attendant Lucas Martin",
                                      "Chief Attendant Grace Lee", "Chief Attendant Alexander Young", "Chief Attendant Chloe King",
                                      "Chief Attendant Benjamin Scott", "Chief Attendant Lily Green", "Chief Attendant Henry Adams",
                                      "Chief Attendant Zoe Baker", "Chief Attendant Jack Hill", "Chief Attendant Nora Carter",
                                      "Chief Attendant Owen Mitchell", "Chief Attendant Ruby Turner", "Chief Attendant Samuel Phillips",
                                      "Chief Attendant Emma Thompson", "Chief Attendant Oliver Davis", "Chief Attendant Sophia Wilson",
                                      "Chief Attendant Noah Martinez", "Chief Attendant Isabella Taylor", "Chief Attendant Lucas Anderson",
                                      "Chief Attendant Olivia Brown", "Chief Attendant Mason Garcia", "Chief Attendant Ava Rodriguez"};
        String[] extraChiefNationalities737 = new String[50];
        for (int i = 0; i < 50; i++) {
            if (i < 20) extraChiefNationalities737[i] = "Turkish";
            else if (i < 35) extraChiefNationalities737[i] = "American";
            else extraChiefNationalities737[i] = "British";
        }
        
        for (int i = 0; i < 50; i++) {
            String attendantId = "A" + String.format("%03d", 300 + i);
            try {
                Optional<Attendant> existing = attendantRepository.findByAttendantId(attendantId);
                Attendant attendant;
                if (existing.isPresent()) {
                    attendant = existing.get();
                } else {
                    attendant = new Attendant();
                    attendant.setAttendantId(attendantId);
                }
                attendant.setName(extraChiefNames737[i]);
                attendant.setAge(30 + (i % 15));
                attendant.setGender(i % 2 == 0 ? "Female" : "Male");
                attendant.setNationality(extraChiefNationalities737[i]);
                attendant.setKnownLanguages("[\"Turkish\", \"English\", \"French\"]");
                attendant.setAttendantType(AttendantType.CHIEF);
                attendant.setVehicleRestrictions("[\"Boeing 737-800\", \"Airbus A320\"]");
                attendant.setIsAvailable(true);
                attendantRepository.save(attendant);
            } catch (Exception e) {
                System.err.println("Warning: Could not save CHIEF " + attendantId + ": " + e.getMessage());
            }
        }

        // Boeing 777-300ER ek CHIEF'ler kaldırıldı

        // Airbus A330 için ek CHIEF'ler (A365'ten başla)
        String[] extraChiefNames330 = {"Senior Chief Andreas Schmidt", "Senior Chief Marie Dubois", "Senior Chief Giovanni Romano",
                                      "Senior Chief Sarah O'Connor", "Senior Chief Lars Johansson", "Senior Chief Elena Vasquez",
                                      "Senior Chief Jean-Pierre Martin", "Senior Chief Ingrid Bergman", "Senior Chief Carlos Rodriguez",
                                      "Senior Chief Anna Kowalczyk", "Senior Chief Sebastian Müller", "Senior Chief Natalia Kowalski"};
        String[] extraChiefNationalities330 = {"German", "French", "Italian", "Irish", "Swedish", "Spanish",
                                              "French", "Swedish", "Spanish", "Polish", "German", "Polish"};
        
        for (int i = 0; i < 12; i++) {
            String attendantId = "A" + String.format("%03d", 365 + i);
            try {
                Optional<Attendant> existing = attendantRepository.findByAttendantId(attendantId);
                Attendant attendant;
                if (existing.isPresent()) {
                    attendant = existing.get();
                } else {
                    attendant = new Attendant();
                    attendant.setAttendantId(attendantId);
                }
                attendant.setName(extraChiefNames330[i]);
                attendant.setAge(33 + (i % 12));
                attendant.setGender(i % 2 == 0 ? "Male" : "Female");
                attendant.setNationality(extraChiefNationalities330[i]);
                attendant.setKnownLanguages("[\"English\", \"French\", \"German\", \"Spanish\"]");
                attendant.setAttendantType(AttendantType.CHIEF);
                attendant.setVehicleRestrictions("[\"Airbus A330\"]");
                attendant.setIsAvailable(true);
                attendantRepository.save(attendant);
            } catch (Exception e) {
                System.err.println("Warning: Could not save CHIEF " + attendantId + ": " + e.getMessage());
            }
        }

        System.out.println("Örnek kabin ekibi verileri yüklendi! Toplam: ~300+ cabin crew (50+ ek CHIEF eklendi)");
    }
}

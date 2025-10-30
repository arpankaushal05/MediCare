package models;

public class Patient {
    private int patientId;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phone;
    private String gender;
    private String bloodGroup;
    private Double heightMeters;
    private String allergies;
    private String disease;

    public Patient(String username, String password, String fullName, String email, String phone,
                   String gender, String bloodGroup, Double heightMeters, String allergies, String disease) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.bloodGroup = bloodGroup;
        this.heightMeters = heightMeters;
        this.allergies = allergies;
        this.disease = disease;
        this.patientId = 0;
    }

    public Patient(int patientId, String username, String password, String fullName, String email, String phone,
                   String gender, String bloodGroup, Double heightMeters, String allergies, String disease) {
        this(username, password, fullName, email, phone, gender, bloodGroup, heightMeters, allergies, disease);
        this.patientId = patientId;
    }

    // Getters
    public int getPatientId() { return patientId; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getGender() { return gender; }
    public String getBloodGroup() { return bloodGroup; }
    public Double getHeightMeters() { return heightMeters; }
    public String getAllergies() { return allergies; }
    public String getDisease() { return disease; }

    // Setters
    public void setPatientId(int patientId) { this.patientId = patientId; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setGender(String gender) { this.gender = gender; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }
    public void setHeightMeters(Double heightMeters) { this.heightMeters = heightMeters; }
    public void setAllergies(String allergies) { this.allergies = allergies; }
    public void setDisease(String disease) { this.disease = disease; }

    @Override
    public String toString() {
        if (fullName == null || fullName.isEmpty()) {
            return username;
        }
        return fullName + " (" + username + ")";
    }
}

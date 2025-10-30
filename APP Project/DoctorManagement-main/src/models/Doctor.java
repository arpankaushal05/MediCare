package models;

public class Doctor {
    private int doctorId;
    private String username;
    private String password;
    private String name;
    private String specialization;
    private String email;
    private String phone;

    public Doctor(String username, String password, String name, String specialization, String email, String phone) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.specialization = specialization;
        this.email = email;
        this.phone = phone;
    }

    public Doctor(int doctorId, String username, String password, String name, String specialization, String email, String phone) {
        this(username, password, name, specialization, email, phone);
        this.doctorId = doctorId;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        if (specialization == null || specialization.isEmpty()) {
            return name;
        }
        return name + " (" + specialization + ")";
    }
}

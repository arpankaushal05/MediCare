# Doctor Management

Java Swing + MySQL desktop application for managing doctors, patients, appointments, and patient visit history.

## Features

1. **Doctor Directory** – add new doctors and view the current roster with contact information.
2. **Patient Registry** – capture patient demographics and credentials.
3. **Appointment Scheduling** – book appointments linking doctors and patients, with notes and schedule overview.
4. **Visit History** – persist diagnosis/treatment notes for every visit (via SQL script and DAO support).

## Tech Stack

| Layer | Technology |
|-------|------------|
| UI    | Java Swing |
| Data Access | JDBC (MySQL Connector/J) |
| Database | MySQL 8.x |

## Prerequisites

- JDK 11 or later on your system (`java -version`).
- MySQL Server with credentials that can create schemas.
- MySQL Connector/J on the classpath when running from an IDE. The DAO layer expects the driver `com.mysql.cj.jdbc.Driver` already available.

## Database Setup

1. Start MySQL and create a user/database if needed.
2. Run the bundled SQL script to create tables and seed sample data:

   ```bash
   mysql -u <username> -p < your_password > < /path/to/project/db/doctor_management.sql
   ```

   The script creates these tables:

   - `doctors`
   - `patients`
   - `appointments`
   - `patient_history`

   It also inserts sample doctors, patients, appointments, and visit history for quick demos.

3. If your MySQL credentials differ from the defaults, update `utils/DBConnection.java` (`URL`, `USERNAME`, `PASSWORD`).

## Building & Running

### Command line

From the project root:

```bash
javac .\dao\*.java .\models\*.java .\utils\*.java .\gui\*.java
java gui.MainFrame
```

The main window exposes navigation buttons for doctors, patients, appointments, and history views.

### IDE

1. Import the project as a plain Java project.
2. Mark `db/doctor_management.sql` as a resource and execute it against your database.
3. Ensure the MySQL connector JAR is available on the module/class path.
4. Run `gui.MainFrame`.

## Configuration Notes

- Images used by the UI live under `images/`; ensure the files stay alongside the compiled classes so `ImageIO` can resolve them.
- All DAO classes handle database resources via try-with-resources; ensure the DB service is reachable to avoid connection failures.
- Credentials are plain strings for simplicity. For production use, secure credential management is recommended.

## Troubleshooting

- **Cannot connect to database**: Verify MySQL is running, the schema exists, and `DBConnection` properties match your environment.
- **Driver not found**: Add MySQL Connector/J to your classpath (`mysql-connector-j-8.x.x.jar`).
- **SQL errors when importing script**: Drop existing tables or schemas that conflict before rerunning the script.

## License

This project inherits the original repository’s license (see source repository if applicable). Contributions should follow the same terms.

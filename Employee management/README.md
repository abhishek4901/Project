# Employee Management (Java Swing + MySQL)

Simple desktop app to add, list, and remove employees using Java Swing and a MySQL database.

## Requirements

- Java 17+ (or 11+ should also work)
- MySQL Server running locally
- MySQL Connector/J (already included: `mysql-connector-j-9.4.0.jar`)

## Database Configuration

Defaults can be overridden with environment variables. By default it will:

- Host: `localhost`
- Port: `3306`
- Database: `employee_mgmt` (auto-created if missing)
- User: `root`
- Password: `abhishek`

Set alternative values via environment variables if needed:

- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASS`

## Build

Double-click or run in terminal from this folder:

```
build.bat
```

## Run

After a successful build:

```
run.bat
```

The UI lets you:

- Add an employee (Name, Email, Department)
- Delete selected employee
- Refresh list

## Notes

- The app will create the `employee_mgmt` database and `employees` table if they do not exist.
- Ensure your MySQL server is running and the credentials are correct.

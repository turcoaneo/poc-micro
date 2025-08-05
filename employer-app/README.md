# Employer microservice

## GraphQL

http://localhost:8093/em/graphiql?path=/graphql

### Get employers
query {
  employers {
    name
    jobs {
      title
      employees {
        name
      }
    }
  }
}

### Get a specific employer by ID
query {
  employer(id: 1) {
    name
    jobs {
      title
    }
  }
}

### Get job titles and hours worked by employees
query {
  employers {
    jobs {
      title
      employees {
        name
        hours
      }
    }
  }
}

### Get detailed employee data for a specific employer
query {
  employer(id: 1) {
    name
    jobs {
      title
      employees {
        employeeId
        name
        hours
      }
    }
  }
}


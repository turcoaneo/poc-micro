# Employer microservice

## GraphQL

http://localhost:8093/em/graphiql?path=/graphql

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

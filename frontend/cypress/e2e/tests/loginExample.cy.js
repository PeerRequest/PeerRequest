describe("Manual Login", () => {

    it("Logging in", () => {
        cy.visit(Cypress.env('auth_base_url') +
            "/realms/" + Cypress.env('auth_realm') +
            "/protocol/openid-connect/auth?response_type=code" +
            "&client_id=" + Cypress.env('auth_client_id') +
            "&scope=openid&redirect_uri=http://localhost:8080/login/oauth2/code/keycloak")
            .get('input[name="username"]').type(Cypress.env("test5-user").username)
            .get('input[name="password"]').type(Cypress.env("test5-user").password)
            .get('input[name="login"]').click()
            .wait(300)
            .origin("http://localhost:8080", () => {
                cy.get('button[id="avatar_with_name"]').click()
            })
    })
})
/// <reference types="cypress"/>
describe('Categories', () => {

    before(() => {
        cy.request('GET', 'http://localhost:8080/test/auth/login?user_id=userID&user_name=AAA&given_name=Max' + '&family_name=Mustermann&email=ichwill@nichtmehr.sob')
            .then((response) => {
                const cookieString = response.headers["set-cookie"][0];
                cy.setCookie('JSESSIONID', cookieString.substring(11, cookieString.length - 18))
            })

            .visit('http://localhost:8080')
            .get('a[href="/categories"]').click()
            .get('button[aria-label="Create Category"]').click()
            .get('input[id="conference_name"]').type("First Conference")
            .get('input[id="conference_year"]').type("2023")
            .get('input[value="INTERNAL"]').click()
            .get('input[aria-label="min_score"]').type("1")
            .get('input[aria-label="max_score"]').type("5")
            .get('input[aria-label="score_step_size"]').type("1")
            .get('button[type="submit"]').click()

            .visit('http://localhost:8080')
            .get('a[href="/categories"]').click()
            .get('button[aria-label="Create Category"]').click()
            .get('input[id="conference_name"]').type("Delete Conference")
            .get('input[id="conference_year"').type("2023")
            .get('input[value="INTERNAL"]').click()
            .get('input[aria-label="min_score"]').type("1")
            .get('input[aria-label="max_score"]').type("5")
            .get('input[aria-label="score_step_size"]').type("1")
            .get('button[type="submit"]').click()

        cy.request('GET', 'http://localhost:8080/logout/')
            .clearCookie('JSESSIONID')

    })

    beforeEach(() => {
        cy.request('GET', 'http://localhost:8080/test/auth/login?user_id=userID&user_name=AAA&given_name=Max' + '&family_name=Mustermann&email=ichwill@nichtmehr.sob')
            .then((response) => {
                const cookieString = response.headers["set-cookie"][0];
                cy.setCookie('JSESSIONID', cookieString.substring(11, cookieString.length - 18))
            })
            .visit('http://localhost:8080')
            .get('a[href="/categories"]').click()
    })

    afterEach(() => {
        cy.request('GET', 'http://localhost:8080/logout/')
            .clearCookie('JSESSIONID')
    })


    it('TC180: Create a category', () => {
        cy
            .get('button[aria-label="Create Category"]').click()
            .get('input[id="conference_name"]').type("Example Conference")
            .get('input[id="conference_year"]').type("2023")
            .get('input[value="INTERNAL"]').click()
            .get('input[aria-label="min_score"]').type("1")
            .get('input[aria-label="max_score"]').type("5")
            .get('input[aria-label="score_step_size"]').type("1")
            .get('button[type="submit"]').click()
            .get('a[aria-label="category_name"]')
            .contains("Example Conference")
            .should('be.visible')
    })

    it('TC 200: Try to create a duplicate category', () => {
        cy
            .get('button[aria-label="Create Category"]').click()
            .get('input[id="conference_name"]').type("Example Conference")
            .get('input[id="conference_year"]').type("2023")
            .get('input[value="INTERNAL"]').click()
            .get('input[aria-label="min_score"]').type("1")
            .get('input[aria-label="max_score"]').type("5")
            .get('input[aria-label="score_step_size"]').type("1")
            .get('button[type="submit"]').click()
            .visit('http://localhost:8080')
            .get('a[href="/categories"]').click()
            .get('button[aria-label="Create Category"]').click()
            .get('input[id="conference_name"]').type("Example Conference")
            .get('input[id="conference_year"').type("2023")
            .get('input[value="INTERNAL"]').click()
            .get('input[aria-label="min_score"]').type("1")
            .get('input[aria-label="max_score"]').type("5")
            .get('input[aria-label="score_step_size"]').type("1")
            .get('button[type="submit"]').click()
            .get('[aria-label="Category already exists"]', {timeout: 1000}).should('be.visible')
    })

    it('TC 220: Delete a category', () => {
        cy
            .get('a[aria-label="category_name"]')
            .contains("Delete Conference").click()
            .get('button[aria-label="Delete Conference"]').click()
            .get('button[aria-label="Confirm deletion"]').click()
            .get('a[aria-label="category_name"]')
            .contains('Delete Conference')
            .should('not.exist')
    })

})
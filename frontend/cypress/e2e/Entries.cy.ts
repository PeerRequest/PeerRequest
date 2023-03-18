/// <reference types="cypress"/>
describe('Entries', () => {

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
            .get('a[aria-label="category_name"]')
            .contains("First Conference").click()
    })

    afterEach(() => {
        cy.request('GET', 'http://localhost:8080/logout/')
            .clearCookie('JSESSIONID')
    })


    it('TC100: Create an entry', () => {
        cy
            .get('button[aria-label="Submit Paper"]').click()
            .get('input[aria-label="entry_title"]').type("Good Paper")
            .get('input[aria-label="authors"]').type("Sarah Smith")
            .get('input[aria-label="file_input"]').selectFile("../public/lorem_ipsum.pdf")
            .get('input[aria-label="open_slots"]').type("3")
            .get('button[type="submit"]').click()
            .get('a[aria-label="paper_name"]')
            .contains("Good Paper")
            .should('be.visible')
    })

    it('TC160: Try to access a paper as a user without permission', () => {
        cy.request('GET', 'http://localhost:8080/logout/')
            .clearCookie('JSESSIONID')
        cy.request('GET', 'http://localhost:8080/test/auth/login?user_id=userID2&user_name=BBB&given_name=Lisa' + '&family_name=Mueller&email=lisamueller@mail.com')
            .then((response) => {
                const cookieString = response.headers["set-cookie"][0];
                cy.setCookie('JSESSIONID', cookieString.substring(11, cookieString.length - 18))
            })
        cy.visit('http://localhost:8080')
            .get('a[href="/categories"]').click()
            .get('a[aria-label="category_name"]')
            .contains('First Conference').click()
            .get('[aria-label="paper_name"]')
            .contains('Good Paper')
            .should('not.have.attr', 'href')
    })

})
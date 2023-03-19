/// <reference types="cypress"/>
describe('Requests', () => {
    before(() => {
        cy.request('GET', 'http://localhost:8080/test/auth/login?user_id=userID&user_name=AAA&given_name=Max&family_name=Mustermann&email=ichwill@nichtmehr.sob')
            .then((response) => {
                const cookieString = response.headers["set-cookie"][0];
                cy.setCookie('JSESSIONID', cookieString.substring(11, cookieString.length - 18))
            })

            // create a category
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

            // create an entry
            .visit('http://localhost:8080')
            .get('a[href="/categories"]').click()
            .get('a[aria-label="category_name"]')
            .contains("First Conference").click()
            .get('button[aria-label="Submit Paper"]').click()
            .get('input[aria-label="entry_title"]').type("Good Paper")
            .get('input[aria-label="authors"]').type("Sarah Smith")
            .get('input[aria-label="file_input"]').selectFile("../public/lorem_ipsum.pdf")
            .get('input[aria-label="open_slots"]').type("5")
            .get('button[type="submit"]').click()
            .wait(3000)

            .request('GET', 'http://localhost:8080/logout/')
            .clearCookie('JSESSIONID')
    })

    beforeEach(() => {
        cy.request('GET', 'http://localhost:8080/test/auth/login?user_id=userID&user_name=AAA&given_name=Max&family_name=Mustermann&email=ichwill@nichtmehr.sob')
            .then((response) => {
                const cookieString = response.headers["set-cookie"][0];
                cy.setCookie('JSESSIONID', cookieString.substring(11, cookieString.length - 18))
            })
            .visit('http://localhost:8080')
            .get('a[href="/categories"]').click()
            .get('a[aria-label="category_name"]')
            .contains("First Conference").click()

            .visit('http://localhost:8080')
    })

    afterEach(() => {
        cy.request('GET', 'http://localhost:8080/logout/')
            .clearCookie('JSESSIONID')
    })


    it('TC240: Send a request & TC250: Accept a request & TC280: Change number of review slots', () => {
        cy
            .get('a[href="/categories"]').click()
            .get('a[aria-label="category_name"]')
            .contains("First Conference").click()
            .get('a[aria-label="paper_name"]')
            .contains("Good Paper").click()
            .get('button[aria-label="Edit Requests"]').click()
            .get('input[aria-label="edit_open_slots"').type("5")
            .get('button[aria-label="Add Reviewer"]').click()
            .get('[aria-label="search_for_reviewer"]').click()
            .type("Helma{enter}")
            .get('[aria-label="click_to_add_reviewer"]')
            .contains("Helma Gunter").click()
            .get('button[type="submit"]').click()
            .wait(1000)

            .request('GET', 'http://localhost:8080/logout/')
            .clearCookie('JSESSIONID')
            .request('GET', 'http://localhost:8080/test/auth/login?user_id=1&user_name=test1&given_name=Helma&family_name=Gunter&email=helma@gunter.de')
            .then((response) => {
                cy.wait(1000)
                const cookieString = response.headers["set-cookie"][0];
                cy.setCookie('JSESSIONID', cookieString.substring(11, cookieString.length - 18))
            })
            .wait(4000)
            .visit('http://localhost:8080')
            .get('a[aria-label="pending_request"]')
            .contains("Good Paper")
            .should('be.visible')
            .get('button[aria-label="accept_request"]').click()
            .wait(5000)
            .get('a[aria-label="accepted_request"]')
            .contains("Good Paper")
            .should('be.visible')
    })

    it('TC330: Display reviews as researcher', () => {
        cy
            .get('a[href="/categories"]').click()
            .get('a[aria-label="category_name"]')
            .contains("First Conference").click()
            .get('a[aria-label="paper_name"]')
            .contains("Good Paper").click()
            .get('a[aria-label="go_to_review"]')
            .should('be.visible')
    })

})
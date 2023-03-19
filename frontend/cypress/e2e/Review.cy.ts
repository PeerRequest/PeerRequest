/// <reference types="cypress"/>
describe('Reviews', () => {
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
            .get('input[id="conference_year"]').type("2024")
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
            .wait(1000)
            .get('a[aria-label="paper_name"]')
            .contains("Good Paper").click()

            // create request
            .get('button[aria-label="Edit Requests"]').click()
            .get('input[aria-label="edit_open_slots"').type("5")
            .get('button[aria-label="Add Reviewer"]').click()
            .get('[aria-label="search_for_reviewer"]').click()
            .type("Helma{enter}")
            .get('[aria-label="click_to_add_reviewer"]')
            .contains("Helma Gunter").click()
            .get('button[type="submit"]').click()
            .wait(1000)

            // switch user
            .request('GET', 'http://localhost:8080/logout/')
            .clearCookie('JSESSIONID')
            .request('GET', 'http://localhost:8080/test/auth/login?user_id=1&user_name=test1&given_name=Helma&family_name=Gunter&email=helma@gunter.de')
            .then((response) => {
                console.log(response)
                const cookieString = response.headers["set-cookie"][0];
                cy.setCookie('JSESSIONID', cookieString.substring(11, cookieString.length - 18))
            })
            .wait(4000)

            //accept request
            .visit('http://localhost:8080')
            .get('a[aria-label="pending_request"]')
            .get('button[aria-label="accept_request"]').click()
            .wait(2000)

            .request('GET', 'http://localhost:8080/logout/')
            .clearCookie('JSESSIONID')
    })

    beforeEach(() => {
        cy.request('GET', 'http://localhost:8080/test/auth/login?user_id=1&user_name=test1&given_name=Helma&family_name=Gunter&email=helma@gunter.de')
            .then((response) => {
                const cookieString = response.headers["set-cookie"][0];
                cy.setCookie('JSESSIONID', cookieString.substring(11, cookieString.length - 18))
            })
            .visit('http://localhost:8080')
    })

    afterEach(() => {
        cy.request('GET', 'http://localhost:8080/logout/')
            .clearCookie('JSESSIONID')
    })

    it('TC480: Edit Review Form as a Reviewer', () => {
        cy.get('button[aria-label="userpill"]').click()
            .get('a[aria-label="my_reviews"]').click()
            .wait(200)
            .click("bottom")
            .get('[aria-label="review_to_paper"]')
            .contains("Good Paper").click()
            .wait(200)
            .get('button[aria-label="mid_confidence"]').click()
            .get('[aria-label="paper_summary"]').type("Paper Summary")
            .get('[aria-label="main_weakness"]').type("Fatal weakness")
            .get('[aria-label="main_strength"]').type("Great Strength")
            .get('[aria-label="other_comments"]').type("First")
            .get('[aria-label="questions_for_authors"]').type("What is a question?")
            .get('button[aria-label="save_review_form"]').click()

            .get('button[aria-label="userpill"]').click()
            .get('a[aria-label="my_reviews"]').click()
            .wait(200)
            .get('[aria-label="review_to_paper"]')
            .contains("Good Paper").click()
            .wait(200)

            .get('[aria-label="selected_confidence"]').contains("MEDIUM")
            .get('[aria-label="paper_summary"]').should('have.value',"Paper Summary")
            .get('[aria-label="main_weakness"]').should('have.value',"Fatal weakness")
            .get('[aria-label="main_strength"]').should('have.value',"Great Strength")
            .get('[aria-label="other_comments"]').should('have.value',"First")
            .get('[aria-label="questions_for_authors"]').should('have.value',"What is a question?")
    });

    it('TC490: Reviewing via PDF file', () => {
        cy.get('button[aria-label="userpill"]').click()
            .get('a[aria-label="my_reviews"]').click()
            .wait(200)
            .click("bottom")
            .get('[aria-label="review_to_paper"]')
            .contains("Good Paper").click()
            .wait(200)
            .get('[aria-label="review_pdf"]').click()
            .get('input[aria-label="review_file_input"]').selectFile("../public/lorem_ipsum.pdf")
            .get('[aria-label="upload_pdf"]').click()
            .wait(2000)
            .get('[aria-label="review_pdf_view"]').should('be.visible')

    });
})
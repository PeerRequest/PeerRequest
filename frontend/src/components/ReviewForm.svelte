<script>
    import { Label, Range, Textarea, ButtonGroup, Button } from "flowbite-svelte";
    import {onMount} from "svelte";
    import {page} from '$app/stores';

    /** @type {import("./$types").PageData} */
    export let data;

    export let minScore;
    export let maxScore;
    export let error = null;

    export let confidence = 0;
    export let confidenceLevels = ["LOW", "MEDIUM" , "HIGH"];
    export let reviewerUser = false;

    export let review = {
        id:null,
        confidence_level: null,
        summary: null,
        main_weaknesses: null,
        main_strengths: null,
        questions_for_authors: null,
        answers_from_authors: null,
        other_comments: null,
        score: minScore
    }

    let edited_summary = review.summary;
    let edited_main_weaknesses = review.main_weaknesses;
    let edited_main_strengths = review.main_strengths;
    let edited_questions_for_authors = review.questions_for_authors;
    let edited_answers_the_authors = review.answers_from_authors;
    let edited_other_comments = review.other_comments;
    let edited_score = review.score;

    let path = $page.url.pathname;


    function editReviewForm() {
        let data = {
            id: review.id,
            confidence_level: confidenceLevels[confidence],
            main_weaknesses: edited_main_weaknesses,
            main_strengths: edited_main_strengths,
            questions_for_authors: edited_questions_for_authors,
            answers_from_authors: edited_answers_the_authors,
            other_comments: edited_other_comments,
            score: edited_score
        };
        fetch("/api/categories/" + data.category_id + "/entries/" + data.paper_id + "/reviews" , {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data),
        })
            .then((response) => response.json())
            .then((response) => {
                if (response.status < 200 || response.status >= 300) {
                    error = "" + response.status + ": " + response.message;
                    console.log(error);
                } else {
                    console.log("Save success")
                }
            })
            .catch(err => console.log(err))
    }

    onMount(() => {
        if (review.confidence_level !== null) {
            confidence = confidenceLevels.findIndex(review.confidence_level)
        }
    })

</script>

<Label>Score: {edited_score} / {maxScore}</Label>
<Range bind:value={edited_score} id="score" max={maxScore} min={minScore} />

<div class="my-4"></div>

<Label class="spacing">Confidence: {confidenceLevels[confidence]}</Label>
<ButtonGroup>
    <Button color="red" outline on:click={() => confidence = 0}>Low</Button>
    <Button color="yellow" outline on:click={() => confidence = 1}>Medium</Button>
    <Button color="green" outline on:click={() => confidence = 2}>High</Button>
</ButtonGroup>
<hr class="my-4">

<Label class="mb-2" for="summary">Summary of the paper</Label>
<Textarea bind:value={edited_summary} placeholder="Summary of the paper" rows="4" disabled={!reviewerUser}/>

<Label class="mb-2" for="weaknesses">Main weaknesses</Label>
<Textarea bind:value={edited_main_weaknesses} placeholder="Main weaknesses" rows="4" disabled={!reviewerUser} />

<Label class="mb-2" for="strengths">Main strengths</Label>
<Textarea bind:value={edited_main_strengths} name="strengths" placeholder="Main strengths" rows="4" disabled={!reviewerUser} />

<Label class="mb-2" for="other_comments">Other comments</Label>
<Textarea bind:value={edited_other_comments} name="other_comments" placeholder="Other comments" rows="4" disabled={!reviewerUser} />

<Label class="mb-2" for="questions">Open questions for the authors</Label>
<Textarea bind:value={edited_questions_for_authors} name="questions" placeholder="Open questions for the authors" rows="4"
          disabled={!reviewerUser}/>

<Label class="mb-2" for="answers">Answers from the authors</Label>
<Textarea bind:value={edited_answers_the_authors} name="answers" placeholder="Answers from the authors" rows="4"
          disabled={reviewerUser}/>
<Button class="w-full m-auto" on:click={() => editReviewForm()}> Save Changes </Button>

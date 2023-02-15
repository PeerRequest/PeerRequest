<script>
    import { Label, Range, Textarea, ButtonGroup, Button } from "flowbite-svelte";
    import {onMount} from "svelte";
    import {page} from '$app/stores';

    export let category= {
        id:null,
        min_score: 0.5,
        max_score: 5,
        score_step_size: 0.25,
    }
    let minScore = category.min_score;
    let maxScore = category.max_score;
    export let error = null;

    export let confidence = 0;
    export let confidenceLevels = ["LOW", "MEDIUM" , "HIGH"];
    export let reviewerUser = true;

    export let review = {
        id:null,
        entry_id:null,
        confidence_level: "",
        summary: "",
        main_weaknesses: "",
        main_strengths: "",
        questions_for_authors: "",
        answers_from_authors: "",
        other_comments: "",
        score: null
    }


    let edited_summary = review.summary;
    let edited_main_weaknesses = review.main_weaknesses;
    let edited_main_strengths = review.main_strengths;
    let edited_questions_for_authors = review.questions_for_authors;
    let edited_answers_the_authors = review.answers_from_authors;
    let edited_other_comments = review.other_comments;
    let edited_score = (review.score !== null) ? review.score : minScore

    let path = $page.url.pathname;


    function editReviewForm() {
        let editedReviewForm = {
            id: review.id,
            confidence_level: confidenceLevels[confidence],
            summary: edited_summary,
            main_weaknesses: edited_main_weaknesses,
            main_strengths: edited_main_strengths,
            questions_for_authors: edited_questions_for_authors,
            answers_from_authors: edited_answers_the_authors,
            other_comments: edited_other_comments,
            score: edited_score
        };
        fetch("/api/categories/" + category.id + "/entries/" + review.entry_id + "/reviews" , {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(editedReviewForm),
        })
            .then((response) => response.json())
            .then((response) => {
                if (response.status < 200 || response.status >= 300) {
                    error = "" + response.status + ": " + response.message;
                    console.log(error);
                } else {
                    alert("Save success")
                }
            })
            .catch(err => console.log(err))

        fetch("/api/categories/" + category.id + "/entries/" + review.entry_id + "/reviews/" + review.id + "/notify", {
            method: "POST"
        })
            .then(resp => resp)
            .then(resp => {
                console.log(resp.status)
                if (resp.status < 200 || resp.status >= 300) {
                    error = "" + resp.status + ": " + resp.message;
                    console.log(error);
                } else {
                }
            })
            .catch(err => console.log(err));
    }

    onMount(() => {
        if (review.confidence_level !== null) {
            confidence = confidenceLevels.indexOf(review.confidence_level)
        }
    })

</script>

<Label>Score: {edited_score} / {maxScore}</Label>
<Range bind:value={edited_score} id="score" max={maxScore} min={minScore} step="{category.score_step_size}" />

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

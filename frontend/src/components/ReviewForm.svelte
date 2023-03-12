<script>
    import {Label, Range, Textarea, Toast, ButtonGroup, Button} from "flowbite-svelte";
    import {onMount} from "svelte";
    import {page} from '$app/stores';

    export let category = {
        id: null,
        min_score: 0.5,
        max_score: 5,
        score_step_size: 0.25,
    }
    let minScore = category.min_score;
    let maxScore = category.max_score;
    export let error = null;

    export let confidence = 0;
    export let confidenceLevels = ["LOW", "MEDIUM", "HIGH"];
    export let reviewerUser = true;

    export let review = {
        id: null,
        entry_id: null,
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

    let show_save_notification = false;
    let counter = 6;

    function triggerNotification() {
        show_save_notification = true;
        counter = 6;
        timeout();
    }
    function timeout() {
        if (--counter > 0)
            return setTimeout(timeout, 1000);
        show_save_notification = false;
    }


    function editReviewForm() {
        if (reviewerUser) {
            let editedReviewForm = {
                id: review.id,
                confidence_level: confidenceLevels[confidence],
                summary: edited_summary,
                main_weaknesses: edited_main_weaknesses,
                main_strengths: edited_main_strengths,
                questions_for_authors: edited_questions_for_authors,
                other_comments: edited_other_comments,
                score: edited_score
            };
            fetch("/api/categories/" + category.id + "/entries/" + review.entry_id + "/reviews", {
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
                        triggerNotification()
                    }
                })
                .catch(err => console.log(err))
        } else {
            let editedReviewForm = {
                id: review.id,
                answers_from_authors: edited_answers_the_authors
            };
            fetch("/api/categories/" + category.id + "/entries/" + review.entry_id + "/reviews", {
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
                        triggerNotification()
                    }
                })
                .catch(err => console.log(err))
        }

    }

    onMount(() => {
        if (review.confidence_level !== null) {
            confidence = confidenceLevels.indexOf(review.confidence_level)
        }
    })

</script>

<Toast color="green" class="mb-2 absolute w-[20vw] top-0 right-[40vw]" bind:open={show_save_notification}>
    <svelte:fragment slot="icon">
        <svg aria-hidden="true" class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg"><path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd"></path></svg>
        <span class="sr-only">Check icon</span>
    </svelte:fragment>
    Save successful
</Toast>
<Button color="primary" class="w-full mb-2" on:click={() => editReviewForm()}> Save Changes</Button>
<Label>Score: {edited_score} / {maxScore}</Label>
<Range bind:value={edited_score} id="score" max={maxScore} min={minScore} step="{category.score_step_size}"
       disabled={!reviewerUser}/>

<div class="my-4"></div>

<Label class="spacing">Confidence: {confidenceLevels[confidence]}</Label>
<ButtonGroup>
    <Button color="red" outline={confidence !== 0} on:click={() => confidence = 0} disabled={!reviewerUser}>Low</Button>
    <Button color="yellow" outline={confidence !== 1} on:click={() => confidence = 1} disabled={!reviewerUser}>Medium</Button>
    <Button color="green" outline={confidence !== 2} on:click={() => confidence = 2} disabled={!reviewerUser}>High</Button>
</ButtonGroup>
<hr class="my-4">

<Label class="mb-2" for="summary">Summary of the paper</Label>
<Textarea bind:value={edited_summary} placeholder="Summary of the paper" rows="4" disabled={!reviewerUser}/>

<Label class="mb-2" for="weaknesses">Main weaknesses</Label>
<Textarea bind:value={edited_main_weaknesses} placeholder="Main weaknesses" rows="4" disabled={!reviewerUser}/>

<Label class="mb-2" for="strengths">Main strengths</Label>
<Textarea bind:value={edited_main_strengths} name="strengths" placeholder="Main strengths" rows="4"
          disabled={!reviewerUser}/>

<Label class="mb-2" for="other_comments">Other comments</Label>
<Textarea bind:value={edited_other_comments} name="other_comments" placeholder="Other comments" rows="4"
          disabled={!reviewerUser}/>

<Label class="mb-2" for="questions">Open questions for the authors</Label>
<Textarea bind:value={edited_questions_for_authors} name="questions" placeholder="Open questions for the authors"
          rows="4"
          disabled={!reviewerUser}/>

<Label class="mb-2" for="answers">Answers from the authors</Label>
<Textarea bind:value={edited_answers_the_authors} name="answers" placeholder="Answers from the authors" rows="4"
          disabled={reviewerUser}/>


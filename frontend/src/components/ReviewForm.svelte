<script>
    import { Label, Range, Textarea, ButtonGroup, Button } from "flowbite-svelte";
    import {onMount} from "svelte";


    export let minScore;
    export let maxScore;

    export let confidence = 0;
    export let confidenceLevels = ["Low", "Medium" , "High"];
    export let reviewerUser = false;

    export let review = {
        "confidence_level": null,
        "summary": null,
        "main_weaknesses": null,
        "main_strengths": null,
        "questions_for_authors": null,
        "answers_from_authors": null,
        "other_comments": null,
        "score": minScore
    }

    let edited_summary = review.summary;
    let edited_main_weaknesses = review.main_weaknesses;
    let edited_main_strengths = review.main_strengths;
    let edited_questions_for_authors = review.questions_for_authors;
    let edited_answers_the_authors = review.answers_from_authors;
    let edited_other_comments = review.other_comments;

    onMount(() => {
        if (review.confidence_level !== null) {
            confidence = confidenceLevels.findIndex(review.confidence_level)
        }
    })

</script>

<Label>Score: {review.score} / {maxScore}</Label>
<Range bind:value={review.score} id="score" max={maxScore} min={minScore} />

<div class="my-4"></div>

<Label class="spacing">Confidence: {confidenceLevels[confidence]}</Label>
<ButtonGroup>
    <Button color="red" outline on:click={() => confidence = 0}>Low</Button>
    <Button color="yellow" outline on:click={() => confidence = 1}>Medium</Button>
    <Button color="green" outline on:click={() => confidence = 2}>High</Button>
</ButtonGroup>
<hr class="my-4">

<Label class="mb-2" for="summary">Summary of the paper</Label>
<Textarea name="summary" placeholder="Summary of the paper" rows="4" value="lol" />

<Label class="mb-2" for="weaknesses">Main weaknesses</Label>
<Textarea id="weaknesses" name="weaknesses" placeholder="Main weaknesses" rows="4" />

<Label class="mb-2" for="strengths">Main strengths</Label>
<Textarea id="strengths" name="strengths" placeholder="Main strengths" rows="4" />

<Label class="mb-2" for="other_comments">Other comments</Label>
<Textarea id="other_comments" name="other_comments" placeholder="Other comments" rows="4" />

<Label class="mb-2" for="questions">Open questions for the authors</Label>
<Textarea id="questions" name="questions" placeholder="Open questions for the authors" rows="4" />

<Label class="mb-2" for="answers">Answers from the authors</Label>
<Textarea id="answers" name="answers" placeholder="Answers from the authors" rows="4" />

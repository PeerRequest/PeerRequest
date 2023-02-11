<script>
    import {
        Modal,
        Button,
        CloseButton,
        Footer
    } from "flowbite-svelte" ;
    import SubmitPaper from "./SubmitPaper.svelte";

    import { createEventDispatcher } from 'svelte';

    const dispatch = createEventDispatcher();

    function submit() {

    }

    export let error;
    export let category;
    export let show = false;
    export let conference_type = "Internal";
    export let hide = () => {
        /* NOP */
    };
    export let result = () => {
        /* NOP */
    }

    export let papers;
    let counter = 0

    function addPaper() {
        papers = papers.concat([counter])
        counter++;
    }

    function finishSubmission() {
        if (papers.length === 0) {
            alert("Submitted nothing!")
        }
        dispatch('finish');
    }



</script>

<Modal bind:open={show} on:hide={() => hide ? hide() : null} size="lg" title="Submit New Paper">
    {#if (category_type === "Internal")}
        <form on:submit={() => {finishSubmission()}}>

        <div class="grid gap-y-6">
                {#each papers as p}
                    <SubmitPaper category={category} submit={submit}/>
                    <hr class="my-8 h-px bg-gray-200 border-0 dark:bg-gray-700">
                {/each}
                <Button on:click={() => addPaper()}> Add Additional Paper</Button>
                {#if (papers.length !== 0)}
                    <Button pill class="!p-2" outline color="red"
                            on:click={() => papers = papers.filter(e => e !== papers[papers.length-1])}>Remove Last
                        Paper
                    </Button>
                {/if}
                <Button type="submit" color="primary" size="xs">
                    Finish Submission
                </Button>
            </div>
        </Footer>
    </form>
</Modal>
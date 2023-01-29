<script>
    import {
        Modal,
        Button,

    } from "flowbite-svelte" ;
    import SubmitPaper from "./SubmitPaper.svelte";
    import PdfUploader from "./PdfUploader.svelte";

    export let show = false;
    export let conference_type = "Internal";
    export let hide = () => {
        /* NOP */
    };
    export let result = () => {
        /* NOP */
    }

    let papers = [];
    let counter = 0


    function addPaper() {
        papers = papers.concat([counter])
        counter++;
    }

    function finishSubmission() {
        if (papers.length === 0) {
            alert("Submitted nothing!")
        }
    }

</script>

<Modal bind:open={show} on:hide={() => hide ? hide() : null} title="Submit New Paper" size="lg">
    <form>
        <div class="grid grid-cols-1 col-span-full gap-y-6 flex justify-center">
            {#each papers as p}
                <SubmitPaper category_type={conference_type}/>
                <hr class="my-8 h-px bg-gray-200 border-0 dark:bg-gray-700">
            {/each}
            <Button on:click={() => addPaper()}> Add Additional Paper</Button>
            {#if (papers.length !== 0)}
                <Button pill class="!p-2" outline color="red"
                        on:click={() => papers = papers.filter(e => e !== papers[papers.length -1])}>Remove Last Paper</Button>
            {/if}
            <Button class="w-full flex-grow" type="submit" color="primary" size="xs" on:click={() => finishSubmission()}>Finish Submission</Button>
        </div>
    </form>
</Modal>
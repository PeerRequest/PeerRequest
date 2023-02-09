<script>
    import {Button, Modal,} from "flowbite-svelte";
    import SubmitPaper from "./SubmitPaper.svelte";
    import PdfUploader from "./PdfUploader.svelte";

    export let category_path;
    export let show = false;
    export let category_type = "Internal";
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
    {#if (category_type === "Internal")}
        <form>
            <div class="grid gap-y-6">
                {#each papers as p}
                    <SubmitPaper/>
                    <hr class="my-8 h-px bg-gray-200 border-0 dark:bg-gray-700">
                {/each}
                <Button on:click={() => addPaper()}> Add Additional Paper</Button>
                {#if (papers.length !== 0)}
                    <Button pill class="!p-2" outline color="red"
                            on:click={() => papers = papers.filter(e => e !== papers[papers.length -1])}>Remove Last Paper</Button>
                {/if}
                <Button type="submit" color="primary" size="xs" on:click={() => finishSubmission()}>Finish Submission</Button>
            </div>
        </form>
    {/if}
    {#if (category_type === "External")}
        <PdfUploader/>
    {/if}
</Modal>
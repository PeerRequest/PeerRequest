<script>
    import {
        Modal,
        Button,
        CloseButton,
        Footer
    } from "flowbite-svelte" ;
    import SubmitPaper from "./SubmitPaper.svelte";

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

<Modal bind:open={show} on:hide={() => hide ? hide() : null} size="lg" permanent = {true} >
    <svelte:fragment slot="header">
        <div class="text-4xl font-extrabold text-gray-900">
            Submit New Paper
        </div>
        <CloseButton class="absolute top-3 right-5"
                     on:click={hide}/>
    </svelte:fragment>
    <form class="min-h-[20vh]">
        <div class="grid grid-cols-1 col-span-full gap-y-6 flex justify-center">
            {#each papers as p}
                <SubmitPaper category_type={conference_type}/>
                <hr class="my-8 h-px bg-gray-200 border-0 dark:bg-gray-700">
            {/each}
        </div>
        <Footer class="absolute bottom-0 left-0 z-20 w-full">
            <div class="grid grid-cols-1 col-span-full gap-y-6 flex justify-center w-full ">
                <Button class="rounded-none h-10 text-sm" outline on:click={() => addPaper()}> Add Additional Paper</Button>
                {#if (papers.length !== 0)}
                    <Button class="!p-2 rounded-none h-10 text-sm" outline color="red"
                            on:click={() => papers = papers.filter(e => e !== papers[papers.length -1])}>Remove Last Paper</Button>
                {/if}
                <Button class="w-full rounded-none h-10 text" type="submit" color="primary" size="sm" on:click={() => finishSubmission()}>Finish Submission</Button>
            </div>
        </Footer>
    </form>
</Modal>
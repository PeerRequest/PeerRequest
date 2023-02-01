<script>
    import {
        Modal,
        Button,
        CloseButton,
        Heading

    } from "flowbite-svelte" ;
    import SubmitPaper from "./SubmitPaper.svelte";
    import PdfUploader from "./PdfUploader.svelte";

    export let show = false;
    export let conference;
    export let hide = () => {
        /* NOP */
    };
    export let result = () => {
        /* NOP */
    }
</script>

<Modal bind:open={show} on:hide={() => hide ? hide() : null} size="lg" permanent={true}>
    <svelte:fragment slot="header">
        <div class="text-4xl font-extrabold text-gray-900">
            Edit Category
        </div>
        <CloseButton class="absolute top-3 right-5"
                     on:click={hide}/>
    </svelte:fragment>
    <form class="grid gap-y-6">
        <div class="flex flex-row justify-between items-center">
            <Heading size="md" tag="h4"> Name </Heading>
            <input class="min-w-[13.5rem]" id= conference_name type= text value={conference.name} size="{conference.name.length}"
                   onkeypress="this.style.width = ((this.value.length + 8) * 8) + 'px';" required>
        </div>
        <div class="flex flex-row justify-between items-center">
            <Heading size="md" tag="h4"> Year </Heading>
            <input id= conference_year type= number value={conference.year} required>
        </div>
        <div class="flex flex-row justify-between items-center">
            <Heading size="md" tag="h4"> Deadline </Heading>
            <input id= conference_deadline type= date value={conference.deadline} required>
        </div>
        <Button type="submit" color="primary" size="xs">
            Save
        </Button>
    </form>
</Modal>
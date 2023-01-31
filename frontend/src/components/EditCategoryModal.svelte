<script>
    import {
        Modal,
        Button,
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

<Modal bind:open={show} on:hide={() => hide ? hide() : null} size="lg">
    <form class="grid gap-y-6">
        <div class="text-4xl font-extrabold text-gray-900">
            Edit Category
        </div>
        <hr/>
        <div class="flex flex-row justify-between items-center">
            <Heading size="md" tag="h4"> Name </Heading>
            <input size="50" id= conference_name type= text value={conference.name} required>
        </div>
        <div class="flex flex-row justify-between items-center">
            <Heading size="md" tag="h4"> Year </Heading>
            <input id= conference_year type= number value={conference.year} required>
        </div>
        <div class="flex flex-row justify-between items-center">
            <Heading size="md" tag="h4"> Deadline </Heading>
            <!-- for now type = text because of mockup, but type = date is more fitting -->
            <input id= conference_deadline type= text value={conference.deadline} required>
        </div>
        <Button type="submit" color="primary" size="xs">
            Save
        </Button>
    </form>
</Modal>
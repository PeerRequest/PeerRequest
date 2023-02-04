<script>
    import {
        Modal,
        Button,
        CloseButton,
        Heading,
        Radio

    } from "flowbite-svelte" ;
    import mock_data from "../mock_data.js";


    export let show = false;
    export let hide = () => {
        /* NOP */
    };
    export let result = () => {
        /* NOP */
    }

    let existing_categories = mock_data.categories;

</script>

<Modal bind:open={show} on:hide={() => hide ? hide() : null} size="lg" permanent={true}>
    <svelte:fragment slot="header">
        <div class="text-4xl font-extrabold text-gray-900">
            Create new Conference
        </div>
        <CloseButton class="absolute top-3 right-5"
                     on:click={hide}/>
    </svelte:fragment>
    <form class="grid gap-y-6">
        <div class="flex flex-row justify-between items-center">
            <Heading size="md" tag="h4"> Name </Heading>
            <input class="min-w-[13.5rem] w-full rounded-lg" id= conference_name type= text
                   onkeypress="this.style.width = ((this.value.length + 8) * 8) + 'px';" required>
        </div>
        <div class="flex flex-row justify-between items-center">
            <Heading size="md" tag="h4"> Year </Heading>
            <input class="w-full rounded-lg" id= conference_year type= number required>
        </div>
        <div class="flex flex-row justify-between items-center">
            <Heading size="md" tag="h4"> Deadline </Heading>
            <input class="w-full rounded-lg" id= conference_deadline type= date required>
        </div>
        <div class="flex flex-row justify-between items-center">
            <Heading size="md" tag="h4"> Conference Type </Heading>
            <div class="flex flex-row w-full justify-evenly">
                <Radio name="conference_type" checked={true}> Internal </Radio>
                <Radio name="conference_type"> External </Radio>
            </div>
        </div>
        <Button type="submit" color="primary" size="xs">
            Save
        </Button>
    </form>
</Modal>
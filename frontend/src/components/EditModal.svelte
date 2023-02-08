<script>
    import {
        Modal,
        Button,
        CloseButton,
        Heading

    } from "flowbite-svelte" ;

    export let show = false;
    export let conference = null;
    export let bidding = null;
    export let urlpath = null;
    export let hide = () => {
        /* NOP */
    };
    export let result = () => {
        /* NOP */
    }

    let edited_category = {
        id: "",
        researcher_id: "",
        name: "",
        year: "",
        label: "",
        deadline: ""
    };
    
    function finishSubmission() {
        edited_category.id = conference.id;
        edited_category.researcher_id = conference.researcher_id;
        edited_category.name = document.getElementById("conference_name");
        edited_category.year = document.getElementById("conference_year");
        edited_category.label = conference.label
        edited_category.deadline = document.getElementById("conference_deadline")

        fetch('/api' + urlpath, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(edited_category),
        })
            .then((response) => response.json())
            .then((response_data) => (response_data))
            .catch(err => console.log(err))
        hide()
    }
</script>

<Modal class="h-full w-full" bind:open={show} on:hide={() => hide ? hide() : null} permanent={true} size="lg">

    <svelte:fragment slot="header">
        <div class="text-4xl font-extrabold text-gray-900">
            Edit {bidding !== null ? "Bidding" : (conference !== null ? "Conference" : "")}
        </div>
        <CloseButton class="absolute top-3 right-5"
                     on:click={hide}/>
    </svelte:fragment>


    {#if conference !== null}
        <form class="grid gap-y-6">
            <div class="flex flex-row justify-between items-center">
                <Heading size="md" tag="h4"> Name</Heading>
                <input class="min-w-[27rem] w-full rounded-lg" id=conference_name type=text value={conference.name}
                       required>
            </div>
            <div class="flex flex-row justify-between items-center">
                <Heading size="md" tag="h4"> Year</Heading>
                <input class="w-full rounded-lg" id=conference_year type=number value={conference.year} required>
            </div>

            <div class="flex flex-row justify-between items-center">
                <Heading size="md" tag="h4"> Deadline</Heading>
                <input class="w-full rounded-lg" id=conference_deadline type=datetime-local value={conference.deadline} required>
            </div>
            <Button type="submit" color="primary" size="xs" on:click={() => finishSubmission()}>
                Save
            </Button>
        </form>
    {:else}
        {#if bidding !== null}
            <form class="grid gap-y-6">
                <div class="flex flex-row justify-between items-center">
                    <Heading size="md" tag="h4"> Deadline</Heading>
                    <input class="w-full rounded-lg" type=date value={bidding.deadline} required>
                </div>
            </form>
        {/if}
    {/if}

</Modal>
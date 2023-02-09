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
    export let hide = () => {
        /* NOP */
    };
    export let result = () => {
        /* NOP */
    }

    let edited_category_year = conference.year;
    let edited_category_name = conference.name;
    let edited_category_deadline = conference.deadline;

    
    function finishSubmission() {

        let data = {
            id: conference.id,
            year: edited_category_year,
            label: conference.label,
            name: edited_category_name,
            deadline: edited_category_deadline + "T00:00:00+01:00",
            min_score: conference.min_score,
            max_score: conference.max_score,
            score_step_size: conference.score_step_size
        };

        fetch('/api/categories', {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data),
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
                <input class="min-w-[27rem] w-full rounded-lg" bind:value={edited_category_name} type=text
                       required>
            </div>
            <div class="flex flex-row justify-between items-center">
                <Heading size="md" tag="h4"> Year</Heading>
                <input class="w-full rounded-lg" bind:value={edited_category_year} type=number required>
            </div>

            <div class="flex flex-row justify-between items-center">
                <Heading size="md" tag="h4"> Deadline</Heading>
                <input class="w-full rounded-lg" bind:value={edited_category_deadline} type=date required>
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
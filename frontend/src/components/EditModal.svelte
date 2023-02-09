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
        if (edited_category_deadline !== conference.deadline) {
           edited_category_deadline = edited_category_deadline + "T00:00:00+01:00";
        }
        let data = {
            id: conference.id,
            year: edited_category_year,
            label: conference.label,
            name: edited_category_name,
            deadline: edited_category_deadline,
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
            .then((response_data) => (hide()))
            .catch(err => console.log(err))
    }
</script>

<Modal class="h-full w-full" bind:open={show} on:hide={() => hide ? hide() : null} permanent={true} size="lg">

    <svelte:fragment slot="header">
        <div class="text-4xl font-extrabold text-gray-900">
            Edit Conference
        </div>
        <CloseButton class="absolute top-3 right-5"
                     on:click={hide}/>
    </svelte:fragment>


    <form on:submit|preventDefault={() => finishSubmission()}>
        <div class="grid gap-y-6">
            <div class="flex flex-row justify-between items-center">
                <Heading size="md" tag="h4"> Name</Heading>
                <input class="min-w-[27rem] w-full rounded-lg" bind:value="{edited_category_name}" type=text required>
            </div>
            <div class="flex flex-row justify-between items-center">
                <Heading size="md" tag="h4"> Year</Heading>
                <input class="w-full rounded-lg" bind:value={edited_category_year} type=number required>
            </div>

            <div class="flex flex-row justify-between items-center">
                <Heading size="md" tag="h4"> Deadline</Heading>
                <input class="w-full rounded-lg" bind:value={edited_category_deadline} type=date>
            </div>
            <Button type="submit" color="primary" size="xs" on:click={() => finishSubmission()}>
                Save
            </Button>
        </div>
    </form>
</Modal>
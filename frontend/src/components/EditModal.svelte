<script>
    import {
        Modal,
        Button,
        CloseButton,
        Heading
    } from "flowbite-svelte" ;
    import {onMount} from "svelte";

    export let error = null;
    export let show = false;
    export let conference = null;
    export let paper = null;
    export let bidding = null;
    export let hide = () => {
        /* NOP */
    };
    export let result = () => {
        /* NOP */
    }

    let edited_category_year = null;
    let edited_category_name = null;
    let edited_category_deadline = null;
    let edited_min_score = null;
    let edited_max_score = null;
    let edited_score_step_size = null;

    let edited_paper_name = null;
    let edited_authors = null;
    function editCategory() {
        let data = {
            id: conference.id,
            year: edited_category_year,
            label: conference.label,
            name: edited_category_name,
            deadline: (edited_category_deadline !== conference.deadline) ?
                edited_category_deadline + "T00:00:00+01:00" : edited_category_deadline,
            min_score: edited_min_score,
            max_score: edited_max_score,
            score_step_size: edited_score_step_size
        };
        fetch('/api/categories', {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data),
        })
            .then((response) => response.json())
            .then((response) => {
                if (response.status < 200 || response.status >= 300) {
                    error = "" + response.status + ": " + response.message;
                    console.log(error);
                } else {
                    try {
                        hide();
                    } catch (error) {
                        console.log(error);
                    }
                }
            })
            .catch(err => console.log(err))
    }

    function editPaper() {
        let data = {
            id: paper.id,
            name: edited_paper_name,
            authors: edited_authors
        };
        fetch('/api/categories/' + paper.category_id + "/entries", {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data),
        })
            .then((response) => response.json())
            .then((response) => {
                if (response.status < 200 || response.status >= 300) {
                    error = "" + response.status + ": " + response.message;
                    console.log(error);
                } else {
                    try {
                        hide();
                    } catch (error) {
                        console.log(error);
                    }
                }
            })
            .catch(err => console.log(err))
    }


    onMount(() => {
        if (conference !== null) {
            edited_category_year = conference.year;
            edited_category_name = conference.name;
            edited_category_deadline = conference.deadline;
            edited_min_score = conference.min_score;
            edited_max_score = conference.max_score;
            edited_score_step_size = conference.score_step_size;
        } else {
            edited_paper_name = paper.name;
            edited_authors = paper.authors === "undefined" ? "" : paper.authors;
        }
    });

</script>

<Modal bind:open={show} class="h-full w-full" on:hide={() => hide ? hide() : null} permanent={true} size="lg">

    <svelte:fragment slot="header">
        <div class="text-4xl font-extrabold text-gray-900">
            {#if (conference !== null)}
                Edit Conference
            {:else}
                Edit Paper
            {/if}
        </div>
        <CloseButton class="absolute top-3 right-5"
                     on:click={hide}/>
    </svelte:fragment>

    {#if (conference !== null)}
        <form on:submit|preventDefault={() => editCategory()}>
            <div class="grid gap-y-6">
                <div class="flex flex-row justify-between items-center">
                    <Heading size="md" tag="h4"> Name</Heading>
                    <input aria-label="edit_category_name" bind:value="{edited_category_name}" class="min-w-[27rem] w-full rounded-lg" required
                           type=text>
                </div>
                <div class="flex flex-row justify-between items-center">
                    <Heading size="md" tag="h4"> Year</Heading>
                    <input aria-label="edit_category_year" bind:value={edited_category_year} class="w-full rounded-lg" required type=number>
                </div>

                <div class="flex flex-row justify-between items-center">
                    <Heading size="md" tag="h4"> Deadline</Heading>
                    <input aria-label="edit_category_deadline" bind:value={edited_category_deadline} class="w-full rounded-lg" type=date>
                </div>
                <div class="flex flex-row justify-between items-center">
                    <Heading size="md" tag="h4">Minimum Score</Heading>
                    <input aria-label="edit_category_min_score" bind:value={edited_min_score} class="w-full rounded-lg" required type=number>
                </div>
                <div class="flex flex-row justify-between items-center">
                    <Heading size="md" tag="h4">Maximum Score</Heading>
                    <input aria-label="edit_category_max_score" bind:value={edited_max_score} class="w-full rounded-lg" required type=number>
                </div>
                <div class="flex flex-row justify-between items-center">
                    <Heading size="md" tag="h4">Score Step Size</Heading>
                    <input aria-label="edit_category_step_size" bind:value={edited_score_step_size} class="w-full rounded-lg" required type=number>
                </div>
                <Button color="primary" size="xs" type="submit">
                    Save
                </Button>
            </div>
        </form>
    {:else}
        <form on:submit|preventDefault={() => editPaper()}>
            <div class="grid gap-y-6">
                <div class="flex flex-row justify-between items-center">
                    <Heading size="md" tag="h4"> Title</Heading>
                    <input bind:value={edited_paper_name} class="w-full rounded-lg" type=text>
                </div>
                <div class="flex flex-row justify-between items-center">
                    <Heading size="md" tag="h4"> Authors</Heading>
                    <input bind:value={edited_authors} class="w-full rounded-lg" type=text>
                </div>
                <Button color="primary" size="xs" type="submit">
                    Save
                </Button>
            </div>
        </form>
    {/if}
</Modal>
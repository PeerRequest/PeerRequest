<script>
    import {
        Modal,
        Button,
        CloseButton,
        Heading,
        Radio,
        Toast,
        Helper
    } from "flowbite-svelte" ;

    let current_user = {
        id: "",
        first_name: "",
        last_name: "",
        email: "",
        account_management_url: "",
    };

    export let error = null;
    let categories = null;
    export let show = false;
    export let hide = () => {
        /* NOP */
    };
    export let result = () => {
        /* NOP */
    }

    export let existing_categories;
    let categories_without_id;
    let already_exists = false;
    const empty = "";


    let new_category_year;
    let new_category_type;
    let new_category_name;
    let new_category_deadline;
    let minScore;
    let maxScore;
    let scoreStepSize;
    let new_category = {
        year: new_category_year,
        type: new_category_type,
        name: new_category_name,
        deadline: new_category_deadline
    }

    let show_conference_notification = false;
    let counter = 3;

    function triggerNotification() {
        show_conference_notification = true;
        counter = 3;
        timeout();
    }

    function timeout() {
        if (--counter > 0)
            return setTimeout(timeout, 1000);
        show_conference_notification = false;
    }

    function finishCreation() {
        new_category = {
            year: document.getElementsByName("")
        }
        existing_categories.forEach(compare)
        if (already_exists) {
            triggerNotification()
        }
    }

    function compare(category) {
        categories_without_id = {
            year: category.year,
            type: category.type,
            name: category.name,
            deadline: category.deadline
        }
        if (categories_without_id === new_category) {
            already_exists = true;
        }
    }

    function resetForm() {
        new_category_year = empty;
        new_category_type = empty;
        new_category_name = empty;
        new_category_deadline = empty;
        minScore = empty;
        maxScore = empty;
        scoreStepSize = empty;
    }


    function createCategory() {
        let data = {
            year: new_category_year,
            label: new_category_type,
            name: new_category_name,
            deadline: (new_category_deadline === undefined ? "" : new_category_deadline + "T00:00:00+01:00"),
            min_score: minScore,
            max_score: maxScore,
            score_step_size: scoreStepSize
        };

        for (const property in data) {
            if (property === undefined) {
                return
            }
        }

        return fetch("/api/categories", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        })
            .then(resp => resp.json())
            .then(resp => {
                if (resp.status === 500) {
                    triggerNotification()
                    hide()
                }
                else if (resp.status < 200 || resp.status >= 300) {
                    error = "" + resp.status + ": " + resp.message;
                    console.log(error);
                } else {
                    try {
                        hide();
                        resetForm();
                    } catch (error) {
                        console.log(error);
                    }
                }
            })
            .catch(err => console.log(err));
    }

</script>


<Toast aria-label="Category already exists" simple={true} color="red" class="mb-2 fixed w-[20vw] bottom-0 right-[40vw] z-50" bind:open={show_conference_notification}>
    <svelte:fragment slot="icon">
        <svg aria-hidden="true" class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20"
             xmlns="http://www.w3.org/2000/svg">
            <path clip-rule="evenodd"
                  d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z"
                  fill-rule="evenodd"></path>
        </svg>
        <span class="sr-only">Error icon</span>
    </svelte:fragment>
    Conference already exists!
</Toast>

<Modal bind:open={show} on:hide={() => hide ? hide() : null} permanent={true} size="lg">
    <svelte:fragment slot="header">
        <div class="text-4xl font-extrabold text-gray-900">
            Create New Conference
        </div>
        <CloseButton class="absolute top-3 right-5"
                     on:click={hide}/>
    </svelte:fragment>
    <form class="grid gap-y-6" on:submit|preventDefault|once={() => {finishCreation();createCategory();}}>
        <div class="flex flex-row justify-between items-center">
            <Heading size="md" tag="h4"> Name</Heading>
            <input bind:value={new_category_name} class="min-w-[13.5rem] w-full rounded-lg" id=conference_name required
                   type=text>
        </div>
        <div class="flex flex-row justify-between items-center">
            <Heading size="md" tag="h4"> Year</Heading>
            <input bind:value={new_category_year} class="w-full rounded-lg" id=conference_year required type=number>
        </div>
        <div class="flex flex-row justify-between items-center">
            <Heading size="md" tag="h4"> Deadline (optional)</Heading>
            <input bind:value={new_category_deadline} class="w-full rounded-lg" id=conference_deadline type=date>
        </div>
        <div class="flex flex-row justify-between items-center">
            <Heading size="md" tag="h4"> Conference Type</Heading>
            <div class="flex flex-row w-full justify-evenly">
                <Radio bind:group={new_category_type} name="category_type" value="INTERNAL">
                    Internal
                </Radio>
                <Radio bind:group={new_category_type} name="category_type" value="EXTERNAL"> External</Radio>
            </div>
        </div>
        <div class="flex flex-row justify-between items-center">
            <Heading size="md" tag="h4">Minimum Score</Heading>
            <input aria-label="min_score" title="The score is the rating the reviewer can give to the papers of this conference.
Minimum score is the minimal value the score can have." bind:value={minScore} max={maxScore - 1} class="w-full rounded-lg" required type=number>
        </div>
        {#if minScore >= maxScore}
            <Helper class="text-red-500" visable={false}><span class="font-medium text-red-500">Warning!</span>
                <br>The minimum score cannot be greater than <br> or equal to the maximum score.
            </Helper>
        {/if}
        <div class="flex flex-row justify-between items-center">
            <Heading size="md" tag="h4">Maximum Score</Heading>
            <input aria-label="max_score" title="The score is the rating the reviewer can give to the papers of this conference.
Maximum score is the maximal value the score can have."  bind:value={maxScore} min={minScore} class="w-full rounded-lg" required type=number>
        </div>
        <div class="flex flex-row justify-between items-center">
            <Heading size="md" tag="h4">Score Step Size</Heading>

            <input aria-label="score_step_size" title="The score step size is the increment of scores the reviewer can give to the papers of this conference.
Example: With a score step size of 1 in the range of 0 to 3 the reviewer can give the scores 0, 1, 2, 3"  bind:value={scoreStepSize} min={1} max={maxScore-minScore} class="w-full rounded-lg" required type=number>

        </div>
        {#if scoreStepSize > maxScore - minScore}
            <Helper class="text-red-500" visable={false}><span class="font-medium text-red-500">Warning!</span>
                <br>The score step size cannot be greater than <br> the difference between the minimum and maximum scores.
            </Helper>
        {/if}
        <Button color="primary" size="xs" type="submit">
            Save
        </Button>
    </form>
</Modal>
<script>
    import {
        Modal,
        Button,
        CloseButton,
        Heading,
        Radio
    } from "flowbite-svelte" ;
    import {page} from "$app/stores";
    import Cookies from "js-cookie";

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

    function finishCreation() {
        new_category = {
            year: document.getElementsByName("")
        }
        existing_categories.forEach(compare)
        if (already_exists) {
            alert("Conference already exists!")
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


    function createCategory() {
        let data = {
            year: new_category_year,
            label: new_category_type,
            name: new_category_name,
            deadline: new_category_deadline + "T00:00:00+01:00",
            min_score: minScore,
            max_score: maxScore,
            score_step_size: scoreStepSize
        };
        return fetch("/api/categories", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        })
            .then(resp => resp.json())
            .then(resp => {})
            .catch(err => console.log(err));
    }

</script>

<Modal bind:open={show} on:hide={() => hide ? hide() : null} permanent={true} size="lg">
    <svelte:fragment slot="header">
        <div class="text-4xl font-extrabold text-gray-900">
            Create new Conference
        </div>
        <CloseButton class="absolute top-3 right-5"
                     on:click={hide}/>
    </svelte:fragment>
    <form class="grid gap-y-6">
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
            <input bind:value={minScore} class="w-full rounded-lg" required type=number>
        </div>
        <div class="flex flex-row justify-between items-center">
            <Heading size="md" tag="h4">Maximum Score</Heading>
            <input bind:value={maxScore} class="w-full rounded-lg" required type=number>
        </div>
        <div class="flex flex-row justify-between items-center">
            <Heading size="md" tag="h4">Score Step Size</Heading>
            <input bind:value={scoreStepSize} class="w-full rounded-lg" required type=number>
        </div>
        <Button color="primary"
                on:click={() => {finishCreation();createCategory();show=false;}}
                size="xs" type="submit">
            Save
        </Button>
    </form>
</Modal>
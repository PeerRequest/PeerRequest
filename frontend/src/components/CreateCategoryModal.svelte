<script>
    import {
        Modal,
        Button,
        CloseButton,
        Heading,
        Radio

    } from "flowbite-svelte" ;

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
        let category = {
            year: new_category_year,
            type: new_category_type,
            name: new_category_name,
            deadline: new_category_deadline
        }
        return fetch("/api/categories", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(category)
        })
            .then(resp => resp.json())
            .then(resp => {
                if (resp.status < 200 || resp.status >= 300) {
                    error = "" + resp.status + ": " + resp.message;
                    console.log(error);
                } else {
                    categories = [...categories, resp.content];
                }
            })
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
                <Radio bind:group={new_category_type} checked={true} name="category_type" value="Internal"> Internal
                </Radio>
                <Radio bind:group={new_category_type} name="category_type" value="External"> External</Radio>
            </div>
        </div>
        <Button color="primary" on:click={() => {finishCreation(); createCategory()}} size="xs" type="submit">
            Save
        </Button>
    </form>
</Modal>
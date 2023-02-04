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

    function compare(category){
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
            <input class="min-w-[13.5rem] w-full rounded-lg" id= conference_name type= text bind:value={new_category_name}
                   onkeypress="this.style.width = ((this.value.length + 8) * 8) + 'px';" required>
        </div>
        <div class="flex flex-row justify-between items-center">
            <Heading size="md" tag="h4"> Year </Heading>
            <input class="w-full rounded-lg" id= conference_year type= number bind:value={new_category_year} required>
        </div>
        <div class="flex flex-row justify-between items-center">
            <Heading size="md" tag="h4"> Deadline </Heading>
            <input class="w-full rounded-lg" id= conference_deadline type= date bind:value={new_category_deadline} required>
        </div>
        <div class="flex flex-row justify-between items-center">
            <Heading size="md" tag="h4"> Conference Type </Heading>
            <div class="flex flex-row w-full justify-evenly">
                <Radio group={new_category_type} value="Internal" checked={true}> Internal </Radio>
                <Radio group={new_category_type} value="External"> External </Radio>
            </div>
        </div>
        <Button type="submit" color="primary" size="xs" on:click={() => finishCreation()}>
            Save
        </Button>
    </form>
</Modal>
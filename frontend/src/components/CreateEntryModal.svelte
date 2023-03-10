<script>
    import {
        Button,
        Chevron,
        CloseButton,
        Dropdown,
        Fileupload,
        Footer,
        Heading,
        Modal,
        Search,
        Table,
        TableBody,
        TableBodyCell,
        TableBodyRow,
        TableHead,
        TableHeadCell
    } from "flowbite-svelte";
    import {onMount} from "svelte";
    import Cookies from "js-cookie";
    import {page} from "$app/stores";

    export let error = null;
    export let category;
    export let show = false;
    export let hide = () => {
        /* NOP */
    };
    export let result = () => {
        /* NOP */
    }

    let query = "";
    export let entries = null;
    let users = null;
    let reviewers = [];
    let authors;
    let name;
    let category_id = category.id;
    let fileInput;
    let file;
    let new_entry_id;
    let current_user;
    let path = $page.url.pathname;
    let open_slots = 0;

    let fileuploadprops = {
        id: "annotations_file_input",
        accept: ".pdf,application/pdf"
    };

    function apply_query(q) {
        query = q;
    }

    function addReviewer(u) {
        reviewers = reviewers.concat([u])
    }

    function createEntry() {
        const input = document.getElementById(fileuploadprops.id);
        file = input.files[0];
        console.log(file);
        const formData = new FormData();
        formData.append("authors", authors);
        formData.append("name", name);
        formData.append("file", file);
        return fetch("/api/categories/" + category.id + "/entries", {
            method: "POST",
            body: formData
        })
            .then(resp => resp.json())
            .then(resp => {
                if (resp.status < 200 || resp.status >= 300) {
                    error = "" + resp.status + ": " + resp.message;
                    console.log(error);
                } else {
                    try {
                        hide();
                        new_entry_id = resp.id;
                        console.log(new_entry_id)
                        createDirectRequestProcess();
                    } catch (error) {
                        console.log(error);
                    }
                }
            })
            .catch(err => console.log(err));
    }


    function createDirectRequestProcess() {
        let data = {
            open_slots: open_slots
        }

        return fetch("/api/categories/" + category.id + "/entries/" + new_entry_id + "/process", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        })
            .then(resp => resp.json())
            .then(resp => {
                if (resp.status < 200 || resp.status >= 300) {
                    error = "" + resp.status + ": " + resp.message;
                    console.log(error);
                } else {
                    reviewers.map(reviewer => createDirectRequest(reviewer.id))
                }
            })
            .catch(err => console.log(err));
    }

    function createDirectRequest(reviewer_id) {
        let data = {
            reviewer_id: reviewer_id
        }
        return fetch("/api/categories/" + category.id + "/entries/" + new_entry_id + "/process/requests", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        })
            .then(resp => resp.json())
            .then(resp => {
                if (resp.status < 200 || resp.status >= 300) {
                    error = "" + resp.status + ": " + resp.message;
                    console.log(error);
                } else {
                }
            })
            .catch(err => console.log(err));
    }

    function loadUsers() {
        users = null;
        fetch("/api/users")
            .then(resp => resp.json())
            .then(resp => {
                if (resp.status < 200 || resp.status >= 300) {
                    error = "" + resp.status + ": " + resp.message;
                    console.log(error);
                } else {
                    users = resp.content;
                    users = users.filter(e => e.id !== current_user.id)
                }
            })
            .catch(err => console.log(err))
    }

    onMount(() => {
        loadUsers()
        current_user = JSON.parse(Cookies.get("current-user") ?? "{}")
        console.log(current_user)
    });

</script>

<Modal bind:open={show} on:hide={() => hide ? hide() : null} permanent={true} size="lg">
    <svelte:fragment slot="header">
        <div class="text-4xl font-extrabold text-gray-900">
            Create New Entry
        </div>
        <CloseButton class="absolute top-3 right-5"
                     on:click={hide}/>
    </svelte:fragment>

    <form class="grid gap-y-6" enctype="multipart/form-data" on:submit|preventDefault|once={() => createEntry()}>
        <div class="flex flex-row justify-between items-center">
            <Heading size="sm" tag="h4"> Enter Paper Title</Heading>
            <input bind:value={name} class="min-w-[13.5rem] w-full rounded-lg" id=entered_entry_title required
                   type=text>
        </div>
        <div class="flex flex-row justify-between items-center">
            <Heading size="sm" tag="h4">Enter Paper Authors</Heading>
            <input bind:value={authors} class="min-w-[13.5rem] w-full rounded-lg" id=entered_entry_authors
                   placeholder="(Optional)" type=text>
        </div>

        <div class="flex flex-row justify-between items-center">
            <Fileupload {...fileuploadprops} bind:value={fileInput}
                        inputClass="annotations_file_input"
                        required
                        size="lg"
            />
        </div>

        <div class="flex flex-row justify-between items-center">
            <Heading class="mr-3" size="sm" tag="h4">Choose Open Slots</Heading>
            <input bind:value={open_slots}
                   class="justify-end rounded-lg"
                   id=selected_open_slots
                   min=0
                   type=number>
        </div>

        <Button color="primary">
            <Chevron>Add Reviewer</Chevron>
        </Button>
        <Dropdown class="overflow-y-auto px-3 pb-3 text-sm h-44" on:show={() => apply_query("")}>
            <div class="p-3" slot="header">
                <Search on:input={(e) => apply_query(e.target.value)} on:keyup={(e) => apply_query(e.target.value)}
                        size="md"/>
            </div>
            {#if users !== null}
                {#each users.filter(u => !reviewers.includes(u) &&
                    (query === "" ||
                        ((u.firstName.toLowerCase().includes(query.toLowerCase())) ||
                            u.lastName.toLowerCase().includes(query.toLowerCase())))) as u}
                    <li class="rounded p-2 hover:bg-gray-100 dark:hover:bg-gray-600 font-semibold">
                    <span class="cursor-pointer" on:click={() => addReviewer(u) }>
                      {u.firstName + " " + u.lastName}
                    </span>
                    </li>
                {/each}
            {:else }
                LOADING USERS
            {/if}
        </Dropdown>
        <div class="mb-4 min-h-[200px] overflow-y-auto  max-h-[20vh]">
            <Table divClass="relative">
                <TableHead>
                    <TableHeadCell>Name</TableHeadCell>
                    <TableHeadCell></TableHeadCell>
                </TableHead>
                <TableBody class="divide-y">
                    {#each reviewers as r }
                        <TableBodyRow>
                            <TableBodyCell>{r.firstName + " " + r.lastName}</TableBodyCell>
                            <TableBodyCell>
                                    <div class="flex justify-end">
                                    <Button color="red" pill size="xs" class="!p-2 w-20 h-7 bg-white" outline
                                            on:click={() => {reviewers = reviewers.filter(e => e !== r)}}>
                                        Remove
                                    </Button>
                                </div>
                            </TableBodyCell>
                        </TableBodyRow>
                    {/each}
                </TableBody>
            </Table>
        </div>
        <Footer class="bottom-0 left-0 z-20 w-full">
            <Button class="w-full" color="primary" size="sm" type="submit">
                Finish Submission
            </Button>
        </Footer>
    </form>
</Modal>
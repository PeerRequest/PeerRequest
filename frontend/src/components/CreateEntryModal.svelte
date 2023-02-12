<script>
    import {
        Modal,
        Button,
        CloseButton,
        Heading,
        TableBodyRow,
        TableHeadCell,
        TableHead,
        Table,
        TableBody,
        TableBodyCell,
        Chevron,
        Dropdown,
        Search
    } from "flowbite-svelte" ;
    import {onMount} from "svelte";
    import Cookies from "js-cookie";

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

    let categoryPath = `/api/categories/${category_id}/entries`;
    let entryPath;
    let requestPath;

    function updatePaths() {
        entryPath = `/api/categories/${category_id}/entries/${new_entry_id}/process`;
        requestPath = `/api/categories/${category_id}/entries/${new_entry_id}/process/requests`;
    }

    function apply_query(q) {
        query = q;
    }

    function addReviewer(u) {
        let inputValOpenSlots = document.getElementById("selected_open_slots").value;
        if (reviewers.length + 1 <= inputValOpenSlots) {
            reviewers = reviewers.concat([u])
        } else {
            alert("Warning! Not enough open slots!")
        }
    }

    function createEntry() {
        file = fileInput.files[0];
        console.log(file);
        const formData = new FormData();
        formData.append("authors", authors);
        formData.append("name", name);
        formData.append("file", file);
        return fetch(categoryPath, {
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
                        updatePaths();
                    } catch (error) {
                        console.log(error);
                    }
                }
            })
            .catch(err => console.log(err));
    }

    function processSubmission() {
        createEntry();
        createDirectRequestProcess();
        reviewers.map(reviewer => createDirectRequest(reviewer.id))
    }

    function createDirectRequestProcess() {
        let data = {
            open_slots: reviewers.length
        }

        return fetch(entryPath, {
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
                    console.log("createDirectRequestProcess", resp)
                }
            })
            .catch(err => console.log(err));
    }

    function createDirectRequest(reviewer_id) {
        let data = {
            reviewer_id: reviewer_id
        }
        return fetch(requestPath, {
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
                    console.log("createDirectRequest", resp)
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

    <form class="grid gap-y-6" enctype="multipart/form-data" on:submit|preventDefault={() => processSubmission()}>
        <div class="flex flex-row justify-between items-center">
            <Heading size="sm" tag="h4"> Enter paper title</Heading>
            <input bind:value={name} class="min-w-[13.5rem] w-full rounded-lg" id=entered_entry_title required
                   type=text>
        </div>
        <div class="flex flex-row justify-between items-center">
            <Heading size="sm" tag="h4">Enter paper authors</Heading>
            <input bind:value={authors} class="min-w-[13.5rem] w-full rounded-lg" id=entered_entry_authors
                   placeholder="(Optional)" type=text>
        </div>

        <div class="flex flex-row justify-between items-center">
            <input bind:this={fileInput} type="file"/>
        </div>

        <div class="flex flex-row justify-between items-center">
            <Heading class="mr-3" size="sm" tag="h4">Choose number of review slots</Heading>
            <input class="justify-end rounded-lg"
                   id=selected_open_slots
                   min={reviewers.length > 0 ? reviewers.length : 1}
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
                    (query === "" || u.name.toLowerCase().includes(query.toLowerCase()))) as u }
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
    </form>

    <div class="mb-4">
        <Table divClass="relative">
            <TableHead>
                <TableHeadCell>Name</TableHeadCell>
            </TableHead>
            <TableBody class="divide-y">
                {#each reviewers as r }
                    <TableBodyRow>
                        <TableBodyCell>{r.firstName + " " + r.lastName}</TableBodyCell>
                        <TableBodyCell>
                            <div class="flex flex-wrap items-center gap-2">
                                <Button pill class="!p-2" outline color="red"
                                        on:click={() => {reviewers = reviewers.filter(e => e !== r)}}>
                                    <svg class="w-5 h-5" xmlns="http://www.w3.org/2000/svg" x="0px" y="0px"
                                         width="32px" height="32px" viewBox="0 0 64 64"
                                         xml:space="preserve">
                          <g>
                            <line fill="none" stroke="#000000" stroke-width="2" stroke-miterlimit="10" x1="18.947"
                                  y1="17.153" x2="45.045"
                                  y2="43.056"/>
                          </g>
                                        <g>
                            <line fill="none" stroke="#000000" stroke-width="2" stroke-miterlimit="10" x1="19.045"
                                  y1="43.153" x2="44.947"
                                  y2="17.056"/>
                          </g>
                      </svg>
                                </Button>
                            </div>
                        </TableBodyCell>
                    </TableBodyRow>
                {/each}
            </TableBody>
        </Table>

    </div>
    <Button color="primary" on:click={() => processSubmission()} size="sm" type="submit">
        Finish Submission
    </Button>


</Modal>
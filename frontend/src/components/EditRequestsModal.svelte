<script>
    import {
        Modal,
        Button,
        CloseButton,
        Footer,
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

    export let show = false;
    export let paper;
    export let hide = () => {
        /* NOP */
    };
    export let result = () => {
        /* NOP */
    }

    export let error = null;

    let query = "";
    let users = null;
    let reviewers = [];
    let current_user;
    let requestPath;
    let requests = null;
    let pending_reviewer = [];
    let new_reviewers = [];
    let slots = null
    let process = null;
    let buttonMessage = "Save & Send Requests"


    function addReviewer(u) {
        reviewers = reviewers.concat([u])
        new_reviewers = new_reviewers.concat([u])
    }

    function apply_query(q) {
        query = q;
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
                    loadRequests()
                }
            })
            .catch(err => console.log(err))
    }

    function createDirectRequest(reviewer) {
        let data = {
            reviewer_id: reviewer.id
        }
        requestPath = "/api/categories/" + paper.category_id + "/entries/" + paper.id + "/process/requests"
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
                    new_reviewers = new_reviewers.filter(new_reviewer => new_reviewer !== reviewer)
                    pending_reviewer = pending_reviewer.concat([reviewer.id])
                    loadRequests()
                }
            })
            .catch(err => console.log(err));
    }

    function loadDirectRequestProcess() {
        process = null;
        if (paper === null || paper.category_id === undefined || paper.id === undefined) {
            return
        }
        fetch("/api/categories/" + paper.category_id + "/entries/" + paper.id + "/process")
            .then(resp => resp.json())
            .then(resp => {
                if (resp.status < 200 || resp.status >= 300) {
                    error = "" + resp.status + ": " + resp.message;
                    console.log(error);
                } else {
                    process = resp;
                    slots = process.open_slots;

                }
            })
            .catch(err => console.log(err))
    }

    function loadRequests() {
        requests = null;
        reviewers = [];
        pending_reviewer = [];
        fetch("/api/categories/" + paper.category_id + "/entries/" + paper.id + "/process/requests")
            .then(resp => resp.json())
            .then(resp => {
                if (resp.status < 200 || resp.status >= 300) {
                    error = "" + resp.status + ": " + resp.message;
                    console.log(error);
                } else {
                    requests = resp.content;
                    requests.forEach(filterUsers)
                }
            })
            .catch(err => console.log(err))
    }

    function filterUsers(r) {
        reviewers = reviewers.concat(users.filter(u => u.id === r.reviewer_id))
        if (r.state === "PENDING") {
            pending_reviewer = pending_reviewer.concat(r.reviewer_id)
        }
    }

    function retractRequest(reviewer) {
        if (pending_reviewer.includes(reviewer.id) && !new_reviewers.includes(reviewer.id)) {
            let reviewer_request = requests.filter(request => request.reviewer_id === reviewer.id)
            fetch("/api/categories/" + paper.category_id + "/entries/" + paper.id + "/process/requests/" + reviewer_request[0].id, {
                method: 'DELETE',
            })
                .then((response) => response.json())
                .then((resp) => {
                    if (resp.status < 200 || resp.status >= 300) {
                        error = "" + resp.status + ": " + resp.message;
                        console.log(error);
                    }
                })
                .catch(err => console.log(err))

        } else {
            new_reviewers = new_reviewers.filter(e => e !== reviewer)
        }
        reviewers = reviewers.filter(e => e !== reviewer)
    }

    function patchOpenSlots() {
        let data = {
            open_slots: slots
        };
        fetch('/api/categories/' + paper.category_id + "/entries/" + paper.id + "/process", {
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
            .then(() => {
                new_reviewers.map(reviewer => createDirectRequest(reviewer))
                if (new_reviewers === []) {
                    hide()
                }
            })
    }

    onMount(() => {
        buttonMessage = "Save & Send Requests"
        loadUsers()
        loadDirectRequestProcess()
        current_user = JSON.parse(Cookies.get("current-user") ?? "{}")

    });

    function sendRequests() {
        buttonMessage = "Sending ..."
        patchOpenSlots()
    }

    $: if (show) {
        buttonMessage = "Save & Send Requests"
    }


</script>

<Modal bind:open={show} class="w-full" on:hide={() => hide ? hide() : null} permanent={true} size="md">
    <svelte:fragment slot="header">
        <div class="text-4xl font-extrabold text-gray-900">
            Edit Requests and Open Slots
        </div>
        <CloseButton class="absolute top-3 right-5"
                     on:click={hide}/>
    </svelte:fragment>

    <div class="flex grid gap-y-6 w-full">
        <div class="flex flex-row justify-between items-center">
            <Heading class="mr-3" size="sm" tag="h4">Open Slots</Heading>
            <input aria-label="edit_open_slots" bind:value={slots}
                   class="justify-end rounded-lg"
                   id=selected_open_slots
                   min=0
                   type=number>
        </div>
        <Button aria-label="Add Reviewer" color="primary">
            <Chevron>Add Reviewer</Chevron>
        </Button>
        <Dropdown class="overflow-y-auto px-3 pb-3 text-sm h-44" on:show={() => apply_query("")}>
            <div class="p-3" slot="header">
                <Search aria-label="search_for_reviewer" on:input={(e) => apply_query(e.target.value)} on:keyup={(e) => apply_query(e.target.value)}
                        size="md"/>
            </div>
            {#if users !== null}
                {#each users.filter(u => !reviewers.includes(u) &&
                    (query === "" ||
                        (u.firstName + " " + u.lastName).toLowerCase().includes(query.toLowerCase()))) as u}
                    <li class="rounded p-2 hover:bg-gray-100 dark:hover:bg-gray-600 font-semibold">
                        <span aria-label="click_to_add_reviewer" class="cursor-pointer" on:click={() => addReviewer(u) }>
                          {u.firstName + " " + u.lastName}
                        </span>
                    </li>
                {/each}
            {:else }
                LOADING USERS
            {/if}
        </Dropdown>

        <div class="mb-4 overflow-y-auto min-h-[200px] max-h-[50vh]">
            <Table divClass="relative">
                <TableHead>
                    <TableHeadCell class="w-[50%]">Name</TableHeadCell>
                    <TableHeadCell>Status</TableHeadCell>
                    <TableHeadCell></TableHeadCell>
                </TableHead>
                <TableBody class="divide-y">
                    {#each reviewers as r }
                        <TableBodyRow>
                            <TableBodyCell>{r.firstName + " " + r.lastName}</TableBodyCell>
                            {#if (pending_reviewer.includes(r.id) || new_reviewers.includes(r))}
                                <TableBodyCell>
                                    PENDING
                                </TableBodyCell>
                                <TableBodyCell>
                                    <div class="flex flex-wrap items-center gap-2">
                                        <Button color="red" pill size="xs" class="!p-2 w-20 h-7 bg-white" outline
                                                on:click={() => retractRequest(r)}>
                                            Remove
                                        </Button>
                                    </div>
                                </TableBodyCell>
                            {/if}
                        </TableBodyRow>
                    {/each}
                </TableBody>
            </Table>
        </div>
        <Footer class="bottom-0 left-0 z-20 w-full">
            <Button class="w-full"
                    color="primary"
                    disabled={buttonMessage === "Sending ..."}
                    on:click|once={() => sendRequests()}
                    size="sm"
                    type="submit">
                {buttonMessage}
            </Button>
        </Footer>
    </div>
</Modal>
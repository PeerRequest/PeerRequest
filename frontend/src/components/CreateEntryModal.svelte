<script>
    import {
        Modal,
        Button,
        CloseButton,
        Heading,
        Radio, TableBodyRow, TableHeadCell, TableHead, Table, TableBody, TableBodyCell, Chevron, Dropdown, Search
    } from "flowbite-svelte" ;
    import mock_data from "../mock_data.js";
    import PdfUploader from "./PdfUploader.svelte";
    import {page} from "$app/stores";

    export let error = null;
    export let category;
    let entries = null;
    export let show = false;
    export let hide = () => {
        /* NOP */
    };
    export let result = () => {
        /* NOP */
    }

    let query = "";

    let users = mock_data.users;
    let reviewers = [];
    let files;

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

    let authors;
    let name;
    let category_id = category.id;
    let category_path = `/api/categories/${category_id}+"/entries"`


    function createEntry() {
        let data = {
            authors: authors,
            name: name,
            category_id: category_id
        };
        return fetch(category_path, {
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
                    try {
                        hide();
                    } catch (error) {
                        console.log(error);
                    }
                }
            })
            .catch(err => console.log(err));
    }

    function sendDirectRequest() {

    }
</script>

<Modal bind:open={show} on:hide={() => hide ? hide() : null} permanent={true} size="lg">
    <svelte:fragment slot="header">
        <div class="text-4xl font-extrabold text-gray-900">
            Create New Entry
        </div>
        <CloseButton class="absolute top-3 right-5"
                     on:click={hide}/>
    </svelte:fragment>

    <form class="grid gap-y-6" on:submit|preventDefault={() => createEntry()}>
        <div class="flex flex-row justify-between items-center">
            <Heading size="md" tag="h4"> Enter Paper Title</Heading>
            <input bind:value={name} class="min-w-[13.5rem] w-full rounded-lg" id=entered_entry_title required
                   type=text>
        </div>
        <div class="flex flex-row justify-between items-center">
            <Heading size="md" tag="h4"> Enter Paper Authors</Heading>
            <input bind:value={authors} class="min-w-[13.5rem] w-full rounded-lg" id=entered_entry_authors
                   placeholder="(Optional)" type=text>
        </div>

        <PdfUploader/>

        <div class="flex flex-row justify-between items-center">
            <Heading size="md" tag="h4">Choose The Number Of Review Slots</Heading>
            <input class="justify-end" id=selected_open_slots min={ reviewers.length > 0 ? reviewers.length : 1 }
                   type=number
                   value=1>
        </div>

        <Button color="primary">
            <Chevron>Add Reviewer</Chevron>
        </Button>
        <Dropdown class="overflow-y-auto px-3 pb-3 text-sm h-44" on:show={() => apply_query("")}>
            <div class="p-3" slot="header">
                <Search on:input={(e) => apply_query(e.target.value)} on:keyup={(e) => apply_query(e.target.value)}
                        size="md"/>
            </div>
            {#each users.filter(u => !reviewers.includes(u) &&
                (query === "" || u.name.toLowerCase().includes(query.toLowerCase()))) as u }
                <li class="rounded p-2 hover:bg-gray-100 dark:hover:bg-gray-600 font-semibold">
                <span class="cursor-pointer" on:click={() => addReviewer(u) }>
                  {u.name}
                </span>
                </li>
            {/each}
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
                        <TableBodyCell>{r.name}</TableBodyCell>
                        <TableBodyCell>
                            <div class="flex flex-wrap items-center gap-2">
                                <Button pill class="!p-2" outline color="red"
                                        on:click={() => reviewers = reviewers.filter(e => e !== r)}>
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
    <Button color="primary" size="sm" type="submit">
        Finish Submission
    </Button>


</Modal>
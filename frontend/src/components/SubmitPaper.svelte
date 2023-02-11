<script>
    import {
        Heading,
        Button,
        Chevron,
        Dropdown,
        Search,
        Table,
        TableHead,
        TableHeadCell,
        TableBody,
        TableBodyRow,
        TableBodyCell
    } from "flowbite-svelte" ;
    import mock_data from "../mock_data.js";
    import PdfUploader from "./PdfUploader.svelte";

    export let category;
    export let error;


    let authors;
    let name;
    let category_id = category.id;
    let category_path = `/api/categories/${category_id}+"/entries"`

    export let show = false;
    export let hide = () => {
        /* NOP */
    };
    export let result = () => {
        /* NOP */
    }
    export let category_type;
    let query = "";
    let users = mock_data.users;
    let reviewers = [];
    let files;

    function apply_query(q) {
        query = q;
    }

    function addReviewer(u) {
        reviewers = reviewers.concat([u]);
    }


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

</script>


<div class="flex flex-row justify-between items-center">
    <Heading size="md" tag="h4"> Enter Paper Title</Heading>
    <input id=entered_entry_title required type=text>
</div>
<div class="flex flex-row justify-between items-center">
    <Heading size="md" tag="h4"> Enter Paper Authors</Heading>
    <input id=entered_entry_authors placeholder="(Optional)" type=text>
</div>

<div class="space-y-6">
    <div class="flex flex-row justify-between items-center">
        <Heading size="md" tag="h4"> Enter Paper Title</Heading>
        <input class="rounded-lg" id=entered_entry_title type=text required>
    </div>
    <div class="flex flex-row justify-between items-center">
        <Heading size="md" tag="h4"> Enter Paper Authors</Heading>
        <input class="rounded-lg" id=entered_entry_authors type=text placeholder="(Optional)">
    </div>

<div class="flex flex-row justify-between items-center">
    <Heading size="md" tag="h4">Choose The Number Of Review Slots</Heading>
    <input class="justify-end" id=selected_open_slots min={ reviewers.length > 0 ? reviewers.length : 1 } type=number
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
    {#each users.filter(u => !reviewers.includes(u) && (query === "" || u.name.toLowerCase().includes(query.toLowerCase()))) as u }
        <li class="rounded p-2 hover:bg-gray-100 dark:hover:bg-gray-600 font-semibold">
                <span class="cursor-pointer" on:click={() => addReviewer(u) }>
                  {u.name}
                </span>
        </li>
    {/each}
</Dropdown>

<div class="h-[50vh]">
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
                                     width="32px" height="32px" viewBox="0 0 64 64" enable-background="new 0 0 64 64"
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
        </Dropdown>

        <div class="h-[50vh]">
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
                                             enable-background="new 0 0 64 64"
                                             xml:space="preserve">
                                  <g>
                                    <line fill="none" stroke="#000000" stroke-width="2" stroke-miterlimit="10"
                                          x1="18.947"
                                          y1="17.153" x2="45.045"
                                          y2="43.056"/>
                                  </g>
                                            <g>
                                    <line fill="none" stroke="#000000" stroke-width="2" stroke-miterlimit="10"
                                          x1="19.045"
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
    {/if}
</div>
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

    const mocks = mock_data.categories;

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
</script>


<div class="flex flex-row justify-between items-center">
    <Heading size="md" tag="h4"> Enter Paper Title</Heading>
    <input id= entered_entry_title type= text required>
</div>
<div class="flex flex-row justify-between items-center">
    <Heading size="md" tag="h4"> Enter Paper Authors</Heading>
    <input id= entered_entry_authors type= text placeholder="(Optional)">
</div>

<PdfUploader/>

{#if category_type === "Internal"}
    <div class="flex flex-row justify-between items-center">
        <Heading size="md" tag="h4">Choose The Number Of <br> Reserved Open Slots</Heading>
        <input class="justify-end" id= selected_open_slots type=number min= { reviewers.length === 0 ? 1 : 0 } value= 1 >
    </div>

    <Button color="primary">
        <Chevron>Add Reviewer</Chevron>
    </Button>
    <Dropdown class="overflow-y-auto px-3 pb-3 text-sm h-44" on:show={() => apply_query("")}>
        <div class="p-3" slot="header">
            <Search on:input={(e) => apply_query(e.target.value)} on:keyup={(e) => apply_query(e.target.value)}
                    size="md" />
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
                                      y2="43.056" />
                              </g>
                                        <g>
                                <line fill="none" stroke="#000000" stroke-width="2" stroke-miterlimit="10" x1="19.045"
                                      y1="43.153" x2="44.947"
                                      y2="17.056" />
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
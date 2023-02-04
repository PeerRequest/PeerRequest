<script>
    import {BreadcrumbItem, Button, Chevron, Dropdown, Search, TableBodyCell, TableBodyRow} from "flowbite-svelte";
    import mock_data from "../mock_data.js";

    export let href;
    export let id;
    export let category = null;
    export let reviewer = null;
    export let paper = null;

    export let assignedReviewer = null;
    export let assignmentOngoing = false;

    export let papers = [];

    let query = "";
    let users = mock_data.users;
    let reviewers = [];

    function apply_query(q) {
        query = q;
    }

</script>


<TableBodyRow>

    {#if paper !== null}
        <TableBodyCell>
            <BreadcrumbItem href="/categories/{paper.category.id}/{paper.id}">{paper.title}</BreadcrumbItem>
        </TableBodyCell>
    {/if}

    <TableBodyCell>
        <BreadcrumbItem href={href}>Review #{id}</BreadcrumbItem>
    </TableBodyCell>

    {#if (reviewer !== null) && (assignmentOngoing === false)}
        <TableBodyCell>
            <BreadcrumbItem>{reviewer}</BreadcrumbItem>
        </TableBodyCell>
    {/if}

    {#if category !== null}
        <TableBodyCell>
            <BreadcrumbItem href="/categories/{category.id}">{category.name}</BreadcrumbItem>
        </TableBodyCell>
    {/if}

    {#if assignmentOngoing}
        {#if assignedReviewer !== null}
            <TableBodyCell>
                <BreadcrumbItem>{assignedReviewer.name}</BreadcrumbItem>
            </TableBodyCell>

            <TableBodyCell>
                <div class="flex flex-wrap items-center gap-2">
                    <Button pill class="!p-2" outline color="red"
                            on:click={() => assignedReviewer = null}>
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

        {:else}
            <TableBodyCell>
                <div>
                    <Button color="primary">
                        <Chevron>Assign Reviewer</Chevron>
                    </Button>
                    <Dropdown class="overflow-y-auto px-3 pb-3 text-sm h-44" on:show={() => apply_query("")}>
                        <div class="p-3" slot="header">
                            <Search on:input={(e) => apply_query(e.target.value)}
                                    on:keyup={(e) => apply_query(e.target.value)} size="md"/>
                        </div>
                        {#each users.filter(u => !reviewers.includes(u) && (query === "" || u.name.toLowerCase().includes(query.toLowerCase()))) as u }
                            <li class="rounded p-2 hover:bg-gray-100 dark:hover:bg-gray-600 font-semibold">
              <span class="cursor-pointer" on:click={() => {reviewers = reviewers.concat([u]); assignedReviewer = u}}>
                {u.name}
              </span>
                            </li>
                        {/each}
                    </Dropdown>
                </div>
            </TableBodyCell>
        {/if}
    {:else}
    {/if}

</TableBodyRow>